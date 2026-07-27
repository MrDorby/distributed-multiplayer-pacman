package it.unibo.controller.client.services;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * Manages the read from file containing the URIs useful for the client.
 */
public class UriManager {
    
    private static final String clientConfig = "it/unibo/clientConfig.json";
    private final UriReader uriReader;

    public UriManager() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream input  = ClassLoader.getSystemResourceAsStream(clientConfig)) {
            uriReader = mapper.readValue(input, UriReader.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * @return the URIs needed by the client.
     */
    public UriReader getURIs() {
        return this.uriReader;
    }

}
