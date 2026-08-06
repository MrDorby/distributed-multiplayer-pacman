package it.unibo.gameservermanager.instantiator;

import com.google.gson.Gson;
import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.dsl.base.ResourceDefinitionContext;
import it.unibo.gameservermanager.dto.GameServerInfo;
import it.unibo.gameservermanager.dto.GameServerInitParameters;
import it.unibo.gameservermanager.dto.GameServerStatus;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerCheckException;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Primary
@Component("kubernetesInstantiator")
public class KubernetesGameServerInstantiator implements GameServerInstantiator {
    private static final int MAX_ALLOCATION_WAITING_ITERATIONS = 10;
    private static final long ALLOCATION_WAITING_TIME_MILLIS = 1000;

    private final KubernetesClient kubernetesClient;
    private final ResourceDefinitionContext gameServerDefinitionContext;
    private final String backupServiceUrl;
    private final String resultsServiceUrl;

    public KubernetesGameServerInstantiator() {
        this.kubernetesClient = new KubernetesClientBuilder().build();
        final CustomResourceDefinition gameServerDefinition = this.kubernetesClient
                .apiextensions()
                .v1()
                .customResourceDefinitions()
                .withName("gameservers.agones.dev")
                .get();
        this.gameServerDefinitionContext = CustomResourceDefinitionContext.fromCrd(gameServerDefinition);
        this.backupServiceUrl = System.getenv("BACKUP_SERVICE_URL");
        this.resultsServiceUrl = System.getenv("RESULTS_SERVICE_URL");
        String msg = usesRemotePersistence() ?
                "Remote persistence variables have been specified. Using GameServers with remote persistence."
                : "One of both of BACKUP_SERVICE_URL and RESULTS_SERVICE_URL have not been specified. " +
                "Using GameServers with local persistence.";
        Logger logger = LoggerFactory.getLogger(KubernetesGameServerInstantiator.class);
        logger.info(msg);
    }

    /**
     * @return whether the created GameServers use remote persistence or not.
     * Remote persistence is used only if both {@code BACKUP_SERVICE_URL} and {@code RESULTS_SERVICE_URL} are set
     * as environment variables.
     */
    private boolean usesRemotePersistence() {
        return this.backupServiceUrl != null && this.resultsServiceUrl != null;
    }

    /**
     * Converts a list to a valid JSON-formatted String.
     * @param list the list to convert
     * @return the JSON-valid String.
     */
    private String getJSONList(List<String> list) {
        Gson gson = new Gson();
        return gson.toJson(list);
    }

    /**
     * Instantiate a GameServer with the specified arguments.
     * @param gameServerArgs the arguments to pass to the GameServer.
     * @return the information about the newly allocated GameServer
     * @throws NullPointerException in case of an error while reading the properties of the created GameServer.
     * @throws GameServerInstantiationException in case the GameServer is not allocated correctly.
     */
    private GameServerInfo instantiateGameServer(List<String> gameServerArgs) throws NullPointerException, GameServerInstantiationException {
        String environmentVariablesJSON = usesRemotePersistence() ?
                ",\"env\": [{" +
                        "\"name\": \"BACKUP_SERVICE_URL\"," +
                        "\"value\": \"" + this.backupServiceUrl + "\"" +
                    "}," +
                    "{" +
                        "\"name\": \"RESULTS_SERVICE_URL\"," +
                        "\"value\": \"" + this.resultsServiceUrl + "\"" +
                "}]"
                : "";
        String gameServerJSON = "{" +
                    "\"apiVersion\": \"agones.dev/v1\"," +
                    "\"kind\": \"GameServer\"," +
                    "\"metadata\": {" +
                        "\"generateName\": \"pacman-server-\"" +
                    "}," +
                    "\"spec\": {" +
                        "\"ports\": [{" +
                            "\"name\": \"default\"," +
                            "\"containerPort\": 7777," +
                            "\"protocol\": \"TCPUDP\"" +
                        "}]," +
                        "\"template\": {" +
                            "\"metadata\": {" +
                                "\"labels\": {" +
                                    "\"app\": \"pacman-game\"," +
                                    "\"role\": \"game-server\"" +
                                "}" +
                            "}," +
                            "\"spec\": {" +
                                "\"containers\": [{" +
                                    "\"name\": \"pacman-game-server\"," +
                                    "\"image\": \"pacman-game-server:latest\"," +
                                    "\"imagePullPolicy\": \"IfNotPresent\"," +
                                    "\"args\": " + getJSONList(gameServerArgs) +
                                    environmentVariablesJSON +
                                "}]" +
                            "}" +
                        "}" +
                    "}" +
                "}";
        final GenericKubernetesResource gameServer = this.kubernetesClient
                .genericKubernetesResources(this.gameServerDefinitionContext)
                .inNamespace("default")
                .load(new ByteArrayInputStream(gameServerJSON.getBytes()))
                .create();
        final String gameServerName = gameServer.getMetadata().getName();
        GenericKubernetesResource gameServerUpdated = getGameServerByName(gameServerName);
        int i = 0;
        // TODO: fix this, it's busy waiting. Use waitUntilCondition() on the GameServer instead.
        while (gameServerIsNotAllocated(gameServerUpdated) && i < MAX_ALLOCATION_WAITING_ITERATIONS) {
            try {
                Thread.sleep(ALLOCATION_WAITING_TIME_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            i++;
            gameServerUpdated = getGameServerByName(gameServerName);
        }
        if (gameServerIsNotAllocated(gameServerUpdated)) {
            throw new GameServerInstantiationException("Timeout while waiting for GameServer allocation.");
        }
        final String IP = gameServerUpdated.get("status", "address");
        final String firstPortName = gameServerUpdated.get("status", "ports", 0, "name");
        int TCPPortIndex;
        int UDPPortIndex;
        if (firstPortName.equals("default-tcp")) {
            TCPPortIndex = 0;
            UDPPortIndex = 1;
        } else {
            TCPPortIndex = 1;
            UDPPortIndex = 0;
        }
        final int TCPPort = gameServerUpdated.get("status", "ports", TCPPortIndex, "port");
        final int UDPPort = gameServerUpdated.get("status", "ports", UDPPortIndex, "port");
        return new GameServerInfo(gameServerName, IP, TCPPort, UDPPort);
    }

    private GenericKubernetesResource getGameServerByName(String name) {
        return this.kubernetesClient
                .genericKubernetesResources(this.gameServerDefinitionContext)
                .inNamespace("default")
                .withName(name)
                .get();
    }

    private boolean gameServerIsNotAllocated(GenericKubernetesResource gameServer) {
        return !gameServer.get("status", "state").toString().equals("Allocated");
    }

    @Override
    public GameServerInfo instantiateNormalGameServer(GameServerInitParameters initParameters) {
        final List<String> argsList = new ArrayList<>();
        argsList.add("--orchestrated");
        if (!usesRemotePersistence()) {
            argsList.add("--local");
        }
        argsList.add(initParameters.matchID());
        argsList.add(initParameters.mapID());
        try {
            return instantiateGameServer(argsList);
        } catch (Exception e) {
            throw new GameServerInstantiationException(e);
        }
    }

    @Override
    public GameServerInfo instantiateRecoveryGameServer(String matchID) {
        final List<String> argsList = new ArrayList<>();
        argsList.add("--recover");
        argsList.add("--orchestrated");
        if (!usesRemotePersistence()) {
            argsList.add("--local");
        }
        argsList.add(matchID);
        try {
            return instantiateGameServer(argsList);
        } catch (Exception e) {
            throw new GameServerInstantiationException(e);
        }
    }

    @Override
    public GameServerStatus getGameServerStatus(String serverName) {
        GenericKubernetesResource gameServer = getGameServerByName(serverName);
        if (gameServer != null) {
            return switch (gameServer.get("status", "state").toString()) {
                case "Allocated" -> GameServerStatus.HEALTHY;
                case "Unhealthy" -> GameServerStatus.UNHEALTHY;
                default -> throw new GameServerCheckException("Invalid GameServer status.");
            };
        } else {
            return GameServerStatus.NOT_FOUND;
        }
    }
}
