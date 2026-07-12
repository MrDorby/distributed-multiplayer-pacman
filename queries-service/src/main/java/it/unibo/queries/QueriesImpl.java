package it.unibo.queries;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * QueriesImpl
 */
@RestController
@RequestMapping(value = "/queries")
public class QueriesImpl implements Queries {

    private static final String AUTHENTICATOR_REQUEST = "http://localhost:8080/auth/token";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public QueriesImpl() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    // TODO: check https://spring.io/guides/gs/consuming-rest
    @Override
    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkTokenPermission(@RequestBody String token) { 
        HttpRequest httpTokenRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(AUTHENTICATOR_REQUEST))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(token))
                                    .build();
        
        try {
            HttpResponse<String> tokenResponse = httpClient.send(httpTokenRequest, HttpResponse.BodyHandlers.ofString());
            if (tokenResponse.statusCode() == HttpStatus.OK.value()) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().body(tokenResponse.body());
        } catch (IOException | InterruptedException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @Override
    @PostMapping(value = "/info", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getPlayerInfo(@RequestBody String username) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPlayerInfo'");
    }
    
    
}
