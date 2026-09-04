package it.unibo.controller.client.services;

import java.net.http.HttpClient;
import java.util.Optional;

import it.unibo.controller.client.common.PlayerStats;
import it.unibo.controller.client.common.ConnectionParameters;
import it.unibo.controller.client.key.KeyManager;

public class ServiceManagerImpl implements ServiceManager {
    private final AuthClient authClient;
    private final QueriesClient queriesClient;
    private final MatchmakerClient matchmakerClient;

    public ServiceManagerImpl() {
        UriReader uri = new UriManager().getURIs();
        HttpClient httpClient = HttpClient.newHttpClient();
        KeyManager keyManager = new KeyManager();
        this.authClient = new AuthClient(httpClient, keyManager, uri);
        this.queriesClient = new QueriesClient(httpClient, keyManager, uri);
        this.matchmakerClient = new MatchmakerClientImpl(httpClient, uri);
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
    public Optional<ConnectionParameters> getGameServerParametersByMatchId() throws Exception {
        return this.matchmakerClient.getServerParametersByMatchId(getCurrentMatchId(), getToken());
    }

    @Override
    public Optional<ConnectionParameters> getGameServerParametersByToken() throws Exception {
        return this.matchmakerClient.getServerParametersByToken(getToken());
    }

    @Override
    public boolean quitMatch() throws Exception {
        return this.matchmakerClient.quitMatch(getToken());
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
