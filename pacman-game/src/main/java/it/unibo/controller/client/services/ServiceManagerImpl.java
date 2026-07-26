package it.unibo.controller.client.services;

import java.net.http.HttpClient;

import it.unibo.controller.client.common.PlayerStats;
import it.unibo.controller.client.common.ConnectionParameters;
import it.unibo.controller.client.key.KeyManager;

public class ServiceManagerImpl implements ServiceManager {
    private static final String MATCHMAKER_URL = "http://localhost:8080"; // TODO change to proper endpoint

    private final HttpClient httpClient;
    private final KeyManager keyManager;
    private final AuthClient authClient;
    private final QueriesClient queriesClient;
    private final MatchmakerClient matchmakerClient;

    public ServiceManagerImpl() {
        this.httpClient = HttpClient.newHttpClient();
        this.keyManager = new KeyManager();
        this.authClient = new AuthClient(this.httpClient, this.keyManager);
        this.queriesClient = new QueriesClient(this.httpClient, this.keyManager);
        this.matchmakerClient = new MatchmakerClientImpl(this.httpClient, MATCHMAKER_URL);
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
    public PlayerStats getPlayerInfo() throws Exception {
        return this.queriesClient.getPlayerStats(getUsername(), getToken());
    }

    @Override
    public boolean queue(String mapName) throws Exception {
        return this.matchmakerClient.queue(mapName, getToken());
    }

    @Override
    public boolean cancelQueue() throws Exception {
        return this.matchmakerClient.cancelQueue(getToken());
    }

    @Override
    public boolean checkQueueStatus() throws Exception {
        return this.matchmakerClient.checkQueueStatus(getToken());
    }

    @Override
    public ConnectionParameters getGameServerParameters() throws Exception {
        return this.matchmakerClient.getServerParameters(getToken());
    }

    @Override
    public String getCurrentLobbyId() {
        return this.matchmakerClient.getCurrentLobbyId();
    }

    @Override
    public String getCurrentMatchId() {
        return this.matchmakerClient.getCurrentMatchId();
    }

    @Override
    public void clearMatchmakingData() {
        this.matchmakerClient.clearMatchmakingData();
    }
}
