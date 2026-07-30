//package master;
//
//import io.kubernetes.client.openapi.ApiException;
//import io.kubernetes.client.openapi.apis.CoreV1Api;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.HttpServerErrorException;
//import org.springframework.web.client.RestClient;
//
//import java.net.InetAddress;
//import java.net.UnknownHostException;
//import java.nio.channels.UnresolvedAddressException;
//
//// TODO: Delete after using as example
//
//@RestController
//@RequestMapping("/master")
//public class ClusterMasterController {
//    @GetMapping("/hello")
//    public Response masterHello() {
//        return new Response("Hello from Master!!!");
//    }
//
//    @GetMapping("/worker")
//    public WorkerInfo getWorkerInfo(@RequestParam(defaultValue = "0") int workerNum) {
//        String workerMessage = "";
//        String workerStatus;
//        String workerIP = "";
//        //final String workerName = "localhost";
//
//        // The request to the cluster-worker service works both with the full domain name (cluster-worker.default.svc.cluster.local)
//        // and with just the service name (cluster-worker).
//        // final String workerName = "cluster-worker.default.svc.cluster.local";
//        final String workerName = "cluster-worker-" + workerNum;
//        final String serviceName = "cluster-worker";
//        final String hostName = workerName + "." + serviceName;
//        final int workerPort = 7000;
//        try {
//            // TODO: it would be good to find a way to capture the UnresolvedAddressException that is thrown
//            //  when this URL is used (especially for debugging purposes when dealing with Kubernetes)
//            workerIP = InetAddress.getByName(hostName).getHostAddress();
//            RestClient restClient = RestClient.create("http://" + hostName + ":" + workerPort + "/worker/hello");
//            Response workerResponse = restClient.get()
//                    .accept(MediaType.APPLICATION_JSON)
//                    .retrieve()
//                    .body(Response.class);
//            if (workerResponse != null) {
//                workerMessage = workerResponse.message();
//                workerStatus = "Available";
//            } else {
//                workerStatus = "Unreachable";
//            }
//            return new WorkerInfo(workerStatus, workerIP, workerPort, workerMessage);
//        } catch (HttpClientErrorException e) {
//            return new WorkerInfo("Unreachable", "", 0, "");
//        } catch (HttpServerErrorException e) {
//            return new WorkerInfo("Unable to respond", "", 0, "");
//        } catch (UnresolvedAddressException e) {
//            return new WorkerInfo("Address unreachable", "", 0, "");
//        } catch (UnknownHostException e) {
//            return new WorkerInfo("Host not found", "", 0, "");
//        }
//    }
//
//    @GetMapping("/pods")
//    public String listAllPods() {
//        CoreV1Api api = new CoreV1Api();
//        try {
//            return api.listPodForAllNamespaces().execute().toString();
//        } catch (ApiException e) {
//            return "Error with the Kubernetes API: " + e.getMessage();
//        }
//    }
//
//    @PostMapping("/worker/create")
//    public ResponseEntity<String> createNewWorker() {
//        CoreV1Api api = new CoreV1Api();
//        //api
//
//        return ResponseEntity.ok("Worker created successfully");
//    }
//}
