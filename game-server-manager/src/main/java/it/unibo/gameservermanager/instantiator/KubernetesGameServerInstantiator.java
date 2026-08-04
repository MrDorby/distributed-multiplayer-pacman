package it.unibo.gameservermanager.instantiator;

import io.fabric8.kubernetes.api.model.GenericKubernetesResource;
import io.fabric8.kubernetes.api.model.apiextensions.v1.CustomResourceDefinition;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.dsl.base.ResourceDefinitionContext;
import it.unibo.gameservermanager.dto.GameServerInfo;
import it.unibo.gameservermanager.dto.GameServerInitParameters;
import it.unibo.gameservermanager.dto.GameServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Primary
@Component("kubernetesInstantiator")
public class KubernetesGameServerInstantiator implements GameServerInstantiator {
    private static final int MAX_ALLOCATION_WAITING_ITERATIONS = 10;
    private static final long ALLOCATION_WAITING_TIME_MILLIS = 1000;

    private final Logger logger = LoggerFactory.getLogger(KubernetesGameServerInstantiator.class);
    private final KubernetesClient kubernetesClient;
    private final CustomResourceDefinition gameServerDefinition;
    private final ResourceDefinitionContext gameServerDefinitionContext;

    public KubernetesGameServerInstantiator() {
        this.kubernetesClient = new KubernetesClientBuilder().build();
        this.gameServerDefinition = this.kubernetesClient
                .apiextensions()
                .v1()
                .customResourceDefinitions()
                .withName("gameservers.agones.dev")
                .get();
        this.gameServerDefinitionContext = CustomResourceDefinitionContext.fromCrd(this.gameServerDefinition);
//        this.gameServerDefinitionContext = new ResourceDefinitionContext.Builder()
//                .withGroup("agones.dev")
//                .withVersion("v1")
//                .withPlural("gameservers")
//                .withNamespaced(true)
//                .build(); TODO: manual resource definition (use if there's no permission to read customresourcedefinitions)
    }

    @Override
    public GameServerInfo instantiateNormalGameServer(GameServerInitParameters initParameters) {
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
                                    "\"args\": [\"--orchestrated\", \"--local\", \"" + initParameters.matchID() + "\", \"" + initParameters.mapID() + "\"]" +
                                    // TODO: specify correct environment variables (BACKUP_SERVICE_URL and RESULTS_SERVICE_URL) during integration
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
        final String name = gameServer.getMetadata().getName();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            this.logger.debug("INTERRUPTED EXCEPTION: {}", e.getMessage()); // TODO: debug
            System.exit(1);
        }
        GenericKubernetesResource gameServerUpdated = getGameServerByName(name);
        int i = 0;
        // TODO: fix this, it's busy waiting
        while (!isGameServerAllocated(gameServerUpdated) && i < MAX_ALLOCATION_WAITING_ITERATIONS) {
            try {
                Thread.sleep(ALLOCATION_WAITING_TIME_MILLIS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            i++;
            gameServerUpdated = getGameServerByName(name);
        }
        final String IP = gameServerUpdated.get("status", "address");
        final int TCPPort = gameServerUpdated.get("status", "ports[?(@.name == \"default-tcp\")]", "port"); // TODO: fix this path (having JsonPath syntax), returns null
        final int UDPPort = gameServerUpdated.get("status", "ports[?(@.name == \"default-udp\")]", "port"); // TODO: fix this path, returns null
        return new GameServerInfo(name, IP, TCPPort, UDPPort); // TODO: obtain connection parameters of created GameServer
    }

    private GenericKubernetesResource getGameServerByName(String name) {
        return this.kubernetesClient
                .genericKubernetesResources(this.gameServerDefinitionContext)
                .inNamespace("default")
                .withName(name)
                .get();
    }

    private boolean isGameServerAllocated(GenericKubernetesResource gameServer) {
        return gameServer.get("status", "state").toString().equals("Allocated");
    }

    @Override
    public GameServerInfo instantiateRecoveryGameServer(String matchID) {
        return null;
    }

    @Override
    public GameServerStatus getGameServerStatus(String serverName) {
        return null;
    }
}
