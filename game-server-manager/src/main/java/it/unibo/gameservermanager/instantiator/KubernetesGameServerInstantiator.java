package it.unibo.gameservermanager.instantiator;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import it.unibo.gameservermanager.dto.GameServerInfo;
import it.unibo.gameservermanager.dto.GameServerInitParameters;
import it.unibo.gameservermanager.dto.GameServerStatus;

import java.util.List;
import java.util.Map;

public class KubernetesGameServerInstantiator implements GameServerInstantiator {
    private final ApiClient kubernetesClient;

    public KubernetesGameServerInstantiator() {
        // TODO: implement proper Kubernetes instantiator (and mark it as @Primary).
//        try {
//            ApiClient kubeClient = ClientBuilder.cluster().build();
//            Configuration.setDefaultApiClient(kubeClient);
//        } catch (IOException e) {
//            logger.error("Error during instantiation of the Kubernetes client: {}", e.getMessage());
//        }
        this.kubernetesClient = Configuration.getDefaultApiClient();
        this.kubernetesClient.setBasePath("http://localhost");
    }

    @Override
    public GameServerInfo instantiateNormalGameServer(GameServerInitParameters initParameters) {
        Map<String, Object> gameServerConfig = Map.of(
            "apiVersion", "agones.dev/v1",
            "kind", "GameServer",
            "metadata", Map.of(
                    "name", "pacman-server"
                ),
            "spec", Map.of(
                    "ports", List.of(Map.of(
                            "name", "default",
                            "containerPort", "7777",
                            "protocol", "TCPUDP"
                        )),
                    "template", Map.of(
                            "metadata", Map.of(
                                    "labels", Map.of(
                                            "app", "pacman-game",
                                            "role", "game-server"
                                        )
                                ),
                            "spec", Map.of(
                                    "containers", List.of(Map.of(
                                            "name", "pacman-game-server",
                                            "image", "pacman-game-server:latest",
                                            "imagePullPolicy", "IfNotPresent",
                                            "args", "", // TODO: specify correct args
                                            "env", ""    // TODO: specify correct environment variables
                                        ))
                                )
                        )
                )
        );
        // TODO: create the GameServer based on the above configuration and obtain its parameters (IP and ports).
        return null;
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
