package it.unibo;

import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class GameServerManagerMain {
    private static final Logger logger = LoggerFactory.getLogger(GameServerManagerMain.class);

    static void main(String[] args) {
        try {
            // TODO: Maybe put this in the GameServerManagerController's constructor
            ApiClient kubeClient = ClientBuilder.cluster().build();
            Configuration.setDefaultApiClient(kubeClient);
        } catch (IOException e) {
            logger.error("Error during instantiation of the Kubernetes client: {}", e.getMessage());
        }
        SpringApplication.run(GameServerManagerMain.class, args);
    }
}
