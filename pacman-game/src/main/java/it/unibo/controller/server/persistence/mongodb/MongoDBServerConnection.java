package it.unibo.controller.server.persistence.mongodb;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import it.unibo.controller.shared.network.dto.PacmanDTO;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.reactivestreams.Publisher;

import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

import it.unibo.controller.server.persistence.dto.MatchSnapshot;
import it.unibo.controller.server.persistence.mongodb.MongoDBConstants.ConnectToDatabase;
import it.unibo.controller.server.persistence.mongodb.MongoDBConstants.LongTermFields;
import it.unibo.controller.server.persistence.mongodb.MongoDBConstants.ShortTermFields;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MongoDBServerConnection manages the GameServer connections with the MongoDB instances.
 */
public class MongoDBServerConnection {

    /* Internal record for make it easy check the content of the short-term database. */
    public record Checkpoint(
        @BsonProperty("gamecontext") GameContextDTO gameContextDTO, 
        @BsonProperty("timestamp") Long timestamp) {
    }

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> collection;
    private final ConnectToDatabase connectToDatabase;

    public MongoDBServerConnection(ConnectToDatabase connectToDatabase) {
        this(connectToDatabase, null);
    }

    public MongoDBServerConnection(ConnectToDatabase connectToDatabase, URI customConnectionURI) {
        this.connectToDatabase = connectToDatabase;
        String connectionUri = (customConnectionURI != null && !customConnectionURI.toString().isBlank())
                ? customConnectionURI.toString()
                : connectToDatabase.getConnectionString();
        this.mongoClient = MongoClients.create(connectionUri);
        this.mongoDatabase = this.mongoClient.getDatabase(connectToDatabase.getDatabaseName());
        this.collection = this.mongoDatabase.getCollection(connectToDatabase.getCollectionName());
    }

    /**
     * This methods handles database save requests, for long-term databases.
     * @param snapshot containing the datas that need to be stored.
     * @return a CompletableFuture[] of the actions done.
     */
    public CompletableFuture<Void> saveResultsOnLongTermDB(MatchSnapshot snapshot) {
        return longTermDB(snapshot.context());
    }

    /**
     * This methods handles database save requests, for short-term databases.
     * @param snapshot containing the datas that need to be stored.
     * @return a CompletableFuture of the action done.
     */
    public CompletableFuture<Void> saveResultsOnShortTermDB(MatchSnapshot snapshot) {
        return shortTermDB(snapshot);
    }

    /*  Performs all the operations with the short-term database. */
    private CompletableFuture<Void> shortTermDB(MatchSnapshot snapshot) {
        ShortTermFields shortTermFields = connectToDatabase.getShortTermFields(); 
        Checkpoint checkpoint = new Checkpoint(snapshot.context(), snapshot.timestamp());
        if (snapshot.matchId() == null || snapshot.matchId().isBlank()) {
            return CompletableFuture.completedFuture(null);
        } 
        Bson filter = eq(shortTermFields.getMatchIdLabel(), snapshot.matchId());
        Publisher<Document> publisher = this.collection.find(filter).first();
        return Mono.from(publisher)
            .flatMap(doc -> {
                List<Checkpoint> checkpoints = (ArrayList<Checkpoint>) doc.get(shortTermFields.getCheckpointsLabel());
                int sizeCheck = 3;
                Bson bson;
                if (checkpoints == null || !doc.containsKey(shortTermFields.getCheckpointsLabel())) {
                    bson = set(shortTermFields.getCheckpointsLabel(), checkpoint);
                } else if (checkpoints != null && checkpoints.size() < sizeCheck) {
                    bson = push(shortTermFields.getCheckpointsLabel(), checkpoint);
                } else {
                    bson = popFirst(shortTermFields.getCheckpointsLabel());
                }
                return Mono.from(this.collection.updateOne(filter, bson)).then();
        }).toFuture();
    }

    /* Handles the requests for the long-term database. */
    private CompletableFuture<Void> longTermDB(GameContextDTO gameContextDTO) {
        LongTermFields longTermFields = this.connectToDatabase.getLongTermFields();
        Map<String, Integer> leaderboard = gameContextDTO.gameState().leaderboard();
        //List<CompletableFuture<Void>> futures = new ArrayList<>(leaderboard.size());
        List<Mono<Void>> monos = new ArrayList<>(leaderboard.size());

        for (var player: leaderboard.entrySet()) {
            Bson filter = eq(longTermFields.getUsernameLabel(), player.getKey());
            List<Bson> updates = new ArrayList<>();
            
            updates.add(inc(longTermFields.getnMatchLabel(), 1));
            updates.add(max(longTermFields.getBestScoreLabel(), player.getValue()));
            
            if (player.getKey().equals(gameContextDTO.gameState().winnerId())) {
                updates.add(inc(longTermFields.getnWinsLabel(), 1));
            }
            
            FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions()
                .upsert(true)
                .returnDocument(ReturnDocument.AFTER);
            
            Publisher<Document> publisher = this.collection
                .findOneAndUpdate(filter, combine(updates), updateOptions);
            
            monos.add(Mono.from(publisher).then());
        }
        return Mono.when(monos).toFuture();
        //return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /*
     * Closes the MongoDB connection.
     */
    public void closeConnection() {
        this.mongoClient.close();
    }

    /**
     * Retrieves the last checkpoint from the short-term mongodb instance.
     * @param matchId the identifier of the match.
     * @return a MatchSnapshot containing all the info for the checkpoint.
     */
    public CompletableFuture<Optional<MatchSnapshot>> getCheckpoint(String matchId) {
        return CompletableFuture.supplyAsync(() -> {
            ShortTermFields shortTermFields = connectToDatabase.getShortTermFields();
            Bson filter = eq(shortTermFields.getMatchIdLabel(), matchId);
            FindPublisher<Document> publisher = (FindPublisher<Document>) this.collection.find(filter).first();
            Document doc = Flux.from(publisher).blockLast();
            if (doc == null || doc.isEmpty()) {
                return Optional.empty();
            }
            List<?> rawCheckpoints = doc.get(shortTermFields.getCheckpointsLabel(), List.class);
            if (rawCheckpoints == null || rawCheckpoints.isEmpty()) {
                return Optional.empty();
            }
            Object lastItem = rawCheckpoints.getLast();
            if (lastItem instanceof Checkpoint(GameContextDTO gameContextDTO, Long timestamp)) {
                MatchSnapshot matchSnapshot = new MatchSnapshot(matchId, timestamp, gameContextDTO);
                return Optional.of(matchSnapshot);
            }
            return Optional.empty();
        });
    }

    /**
     * Retrieves the list containing the usernames of the players.
     * @param matchId the identifier of the match.
     * @return a List of Strings containing the users identifiers.
     */
    public CompletableFuture<Optional<List<String>>> retrievePlayers(String matchId) {
        return CompletableFuture.supplyAsync(() -> {
            ShortTermFields shortTermFields = connectToDatabase.getShortTermFields();
            Bson filter = eq(shortTermFields.getMatchIdLabel(), new ObjectId(matchId));
            Publisher<Document> publisher = this.collection.find(filter).first();
            Document doc = Mono.from(publisher).block();
            if (doc == null || doc.isEmpty()) {
                return Optional.empty();
            }
            List<String> players = (List<String>) doc.get(shortTermFields.getUserListLabel());
            if (players == null || players.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(players);
        });
    }
}
