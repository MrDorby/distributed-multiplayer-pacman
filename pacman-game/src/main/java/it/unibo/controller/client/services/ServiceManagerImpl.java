package it.unibo.controller.client.services;

import java.net.http.HttpClient;

import it.unibo.controller.client.common.Stats;
import it.unibo.controller.client.key.KeyManager;

public class ServiceManagerImpl implements ServiceManager {

    private final HttpClient httpClient;
    private final KeyManager keyManager;
    private final AuthClient authClient;
    private final QueriesClient queriesClient;

    public ServiceManagerImpl() {
        this.httpClient = HttpClient.newHttpClient();
        this.keyManager = new KeyManager();
        this.authClient = new AuthClient(this.httpClient, this.keyManager);
        this.queriesClient = new QueriesClient(this.httpClient, this.keyManager);
    }

    @Override
    public String getUsername() {
        return this.authClient.getUsername();
    }

    @Override
    public String getToken() {
        return this.authClient.getToken();
    }

    @Override
    public void login(String username, String password) throws Exception {
        this.authClient.login(username, password);
    }

    @Override
    public String register(String username, String password) throws Exception {
        return this.authClient.register(username, password);
    }

    @Override
    public Stats getPlayerInfo() throws Exception {
        return this.queriesClient.getPlayerStats(getUsername(), getToken());
    }
    
}
