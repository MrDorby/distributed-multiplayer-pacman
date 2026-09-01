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
import it.unibo.gameservermanager.utils.UriValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Primary
@Component("kubernetesInstantiator")
public class KubernetesGameServerInstantiator implements GameServerInstantiator {
    private static final long ALLOCATION_WAITING_TIME_SECONDS = 15;
    private static final String DEFAULT_GAMESERVER_IMAGE_NAME = "pacman-game-server:latest";
    private static final String GAMESERVER_MANAGER_URI_ENV_NAME = "GAMESERVER_MANAGER_URI";

    private final KubernetesClient kubernetesClient;
    private final ResourceDefinitionContext gameServerDefinitionContext;
    private final String gameServerImageName;
    private final String shortTermDbUri;
    private final String longTermDbUri;
    private final String gameServerManagerUri;

    public KubernetesGameServerInstantiator() {
        this.kubernetesClient = new KubernetesClientBuilder().build();
        final CustomResourceDefinition gameServerDefinition = this.kubernetesClient
                .apiextensions()
                .v1()
                .customResourceDefinitions()
                .withName("gameservers.agones.dev")
                .get();
        this.gameServerDefinitionContext = CustomResourceDefinitionContext.fromCrd(gameServerDefinition);
        Logger logger = LoggerFactory.getLogger(KubernetesGameServerInstantiator.class);
        this.gameServerManagerUri = System.getenv(GAMESERVER_MANAGER_URI_ENV_NAME);
        if(this.gameServerManagerUri == null) {
            throw new IllegalStateException("Environment variable " + GAMESERVER_MANAGER_URI_ENV_NAME + " must be set.");
        }
        UriValidator.validateURI(this.gameServerManagerUri, GAMESERVER_MANAGER_URI_ENV_NAME);
        String gameServerImageName = System.getenv("GAMESERVER_IMAGE_NAME");
        this.gameServerImageName = gameServerImageName != null ? gameServerImageName : DEFAULT_GAMESERVER_IMAGE_NAME;
        logger.info("Using GameServer image: {}", this.gameServerImageName);
        this.shortTermDbUri = System.getenv("SHORT_TERM_DB_URI");
        this.longTermDbUri = System.getenv("LONG_TERM_DB_URI");
        String msg = usesRemotePersistence() ?
                "Remote persistence variables have been specified. Using GameServers with remote persistence."
                : "One of both of SHORT_TERM_DB_URI and LONG_TERM_DB_URI have not been specified. " +
                "Using GameServers with local persistence.";
        logger.info(msg);
    }

    /**
     * @return whether the created GameServers use remote persistence or not.
     * Remote persistence is used only if both {@code SHORT_TERM_DB_URI} and {@code LONG_TERM_DB_URI} are set
     * as environment variables.
     */
    private boolean usesRemotePersistence() {
        return this.shortTermDbUri != null && this.longTermDbUri != null;
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
        String environmentVariablesJSON = getEnvironmentVariablesJSON();
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
                                    "\"image\": \"" + this.gameServerImageName + "\"," +
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
        GenericKubernetesResource gameServerUpdated = waitUntilGameServerIsAllocated(gameServerName);
        if (gameServerUpdated == null) {
            throw new GameServerInstantiationException("The GameServer was not found or could not be allocated.");
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

    private String getEnvironmentVariablesJSON() {
        String dbEnvironmentVariablesJSON = ",{" +
                "\"name\": \"SHORT_TERM_DB_URI\"," +
                "\"value\": \"" + this.shortTermDbUri + "\"" +
                "}," +
                "{" +
                "\"name\": \"LONG_TERM_DB_URI\"," +
                "\"value\": \"" + this.longTermDbUri + "\"" +
                "}";
        return ",\"env\": [{" +
                "\"name\": \"GAMESERVER_MANAGER_URI\"," +
                "\"value\": \"" + this.gameServerManagerUri + "\"" +
                "}" +
                (usesRemotePersistence() ? dbEnvironmentVariablesJSON : "") +
                "]";
    }

    /**
     * Waits until the specified GameServer's status becomes Allocated and returns its information.
     * @param name the GameServer's name.
     * @return the resource containing the information about the GameServer, or {@code null} if the GameServer was not
     * found, or the waiting time for the allocation has expired.
     */
    private GenericKubernetesResource waitUntilGameServerIsAllocated(String name) {
        return this.kubernetesClient
                .genericKubernetesResources(this.gameServerDefinitionContext)
                .inNamespace("default")
                .withName(name)
                .waitUntilCondition(this::isGameServerAllocated, ALLOCATION_WAITING_TIME_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Gets the GameServer with the specified name.
     * @param name the GameServer's name.
     * @return the resource containing the information about the GameServer.
     */
    private GenericKubernetesResource getGameServerByName(String name) {
        return this.kubernetesClient
                .genericKubernetesResources(this.gameServerDefinitionContext)
                .inNamespace("default")
                .withName(name)
                .get();
    }

    /**
     * @param gameServer the GameServer to check.
     * @return true if the GameServer's status is Allocated, false otherwise.
     */
    private boolean isGameServerAllocated(GenericKubernetesResource gameServer) {
        return gameServer.get("status", "state").toString().equals("Allocated");
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
