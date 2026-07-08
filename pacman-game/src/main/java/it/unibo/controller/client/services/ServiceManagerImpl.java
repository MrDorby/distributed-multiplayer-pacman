package it.unibo.controller.client.services;

import java.net.http.HttpClient;

public class ServiceManagerImpl implements ServiceManager {

    private final HttpClient httpClient;
    private final AuthClient authClient;

    public ServiceManagerImpl() {
        this.httpClient = HttpClient.newHttpClient();
        this.authClient = new AuthClient(this.httpClient);
    }

    @Override
    public String getUsername() {
        return this.authClient.getUsername();
    }

    @Override
    public String getToken() {
        return this.authClient.getToken();
    }

    // TODO: Create a Auth Client.
    @Override
    public void login(String username, String password) throws Exception {
        this.authClient.login(username, password);
    }

    @Override
    public String register(String username, String password) throws Exception {
        return this.authClient.register(username, password);
    }
    
}
