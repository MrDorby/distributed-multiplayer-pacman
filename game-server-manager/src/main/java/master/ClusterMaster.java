//package master;
//
//import io.kubernetes.client.openapi.ApiClient;
//import io.kubernetes.client.openapi.Configuration;
//import io.kubernetes.client.util.ClientBuilder;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.io.IOException;
//
//@SpringBootApplication
//public class ClusterMaster {
//    static void main(String[] args) {
//        try {
//            ApiClient kubeClient = ClientBuilder.cluster().build();
//            Configuration.setDefaultApiClient(kubeClient);
//        } catch (IOException e) {
//            System.err.println("Error during instantiation of the Kubernetes client: " + e.getMessage());
//        }
//        SpringApplication.run(ClusterMaster.class, args);
//    }
//}
