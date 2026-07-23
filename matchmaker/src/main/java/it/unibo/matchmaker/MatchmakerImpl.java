package it.unibo.matchmaker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

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
 * MatchmakerImpl
 */
@RestController
@RequestMapping(value = "/matchmaker")
public class MatchmakerImpl implements Matchmaker{

    private static final String AUTHENTICATOR_REQUEST = "http://localhost:8080/auth/token";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public MatchmakerImpl() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    @PostMapping(value = "/join_lobby", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> joinLobby(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'joinLobby'");
    }

    @Override
    @PostMapping(value = "/quit_lobby", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> quitLobby(String token) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'quitLobby'");
    }

    /*  */
    private void checkTokenPermission(String token) throws Exception { 
        HttpRequest httpTokenRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(AUTHENTICATOR_REQUEST))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(token))
                                    .build();
        
        try {
            HttpResponse<String> tokenResponse = httpClient.send(httpTokenRequest, HttpResponse.BodyHandlers.ofString());
            if (tokenResponse.statusCode() == 200) {
                throw new Exception(tokenResponse.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new Exception(e.getMessage());
        }
    }

    
}
