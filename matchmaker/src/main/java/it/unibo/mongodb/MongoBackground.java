package it.unibo.mongodb;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import it.unibo.dto.GameServerResponse;

@Service
public class MongoBackground {
    //TODO: DOCS
    @Async
    public void saveMatchInfo(
        LobbyInfoMongoDB lobby, 
        GameServerResponse gameServer,
        ShortTermLobbyRepository lobbyCollection,
        ShortTermMatchRepository matchCollection) {

        lobby.setMatchId(gameServer.matchId());
        lobbyCollection.save(lobby);
            
        matchCollection.save(
            new MatchInfoMongoDB(gameServer.matchId(), lobby.getPlayers(), gameServer.gameServer()));
    }

    @Async
    public void deleteLobby(LobbyInfoMongoDB lobby, ShortTermLobbyRepository lobbyCollection) {
        lobby.setCounter(lobby.getCounter() + 1);
        if (lobby.getCounter() == lobby.getPlayers().size()) {
            lobbyCollection.delete(lobby);
        } else {
            lobbyCollection.save(lobby);
        }
    }
}
