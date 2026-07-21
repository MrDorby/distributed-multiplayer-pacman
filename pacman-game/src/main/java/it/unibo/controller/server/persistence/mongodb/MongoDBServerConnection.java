package it.unibo.controller.server.persistence.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.reactivestreams.Publisher;

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

/**
 * MongoDBServerConnection manages the GameServer connections with the MongoDB instances.
 */
public class MongoDBServerConnection {

    /* Internal record for make it easy check the content of the short-term database. */
    private record Checkpoint(GameContextDTO gameContextDTO, Long timestamp) {
    }

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> collection;
    private final ConnectToDatabase connectToDatabase;

    public MongoDBServerConnection(ConnectToDatabase connectionString) {
        this.connectToDatabase = connectionString;
        this.mongoClient = MongoClients.create(connectionString.getConnectionString());
        this.mongoDatabase = this.mongoClient.getDatabase(connectionString.getDatabaseName());
        this.collection = this.mongoDatabase.getCollection(connectionString.getCollectionName());
    }
    //TODO: ADD METHOD TO GET CONTEXT FROM SHORT-TERM DB.
    /**
     * This methods handles database save requests, for long-term databases.
     * @param snapshot containing the datas that need to be stored.
     * @return a CompletableFuture[] of the actions done.
     */
    public CompletableFuture<?>[] saveResultsOnLongTermDB(MatchSnapshot snapshot) {
        return longTermDB(snapshot.context());
    }

    /**
     * This methods handles database save requests, for short-term databases.
     * @param snapshot containing the datas that need to be stored.
     * @return a CompletableFuture of the action done.
     */
    public CompletableFuture<?> saveResultsOnShortTermDB(MatchSnapshot snapshot) {
        return shortTermDB(snapshot);
    }

    /*  Performs all the operations with the short-term database. */
    private CompletableFuture<?> shortTermDB(MatchSnapshot snapshot) {
        ShortTermFields shortTermFields = connectToDatabase.getShortTermFields(); 
        //ObjectMapper objectMapper = new ObjectMapper();
        CompletableFuture<?> future = null;
        //try {
            Checkpoint checkpoint = new Checkpoint(snapshot.context(), snapshot.timestamp());
            //String jsonCheckpoint = objectMapper.writeValueAsString(checkpoint);
            // TODO: Check if it is necessary to perform the mapping to JSON for storing data.
            Bson filter = eq(shortTermFields.getMatchIdLabel(), snapshot.matchId());
            FindPublisher<Document> publisher = (FindPublisher<Document>) this.collection.find(filter).first();
            Document doc = Flux.from(publisher).blockLast();
            if (doc.isEmpty()) {
                //Document newDoc = Document.parse(jsonSnap);
                Document newDoc = new Document(shortTermFields.getMatchIdLabel(), snapshot.matchId())
                                .append(shortTermFields.getCheckpointsLabel(), List.of(checkpoint));
                Publisher<InsertOneResult> insertPublisher = this.collection.insertOne(newDoc);
                future = Mono.from(insertPublisher).toFuture();
            } else {
                List<Checkpoint> checkpoints = (ArrayList<Checkpoint>) doc.get(shortTermFields.getCheckpointsLabel());
                int sizeCheck = 3;
                if (checkpoints.size() < sizeCheck) {
                    Bson update = push(shortTermFields.getCheckpointsLabel(), checkpoint);
                    Publisher<UpdateResult> result = this.collection.updateOne(filter, update);
                    future = Mono.from(result).toFuture();
                } else {
                    Bson pop = popFirst(shortTermFields.getCheckpointsLabel());
                    Publisher<UpdateResult> result = this.collection.updateOne(filter, pop);
                    future = Mono.from(result).toFuture();
                }
            }
            return future;
        // } catch (JsonProcessingException e) {
        //     return null;
        // }
    }

    // TODO: Adds comments in the code.
    /* Handles the requests for the long-term database. */
    private CompletableFuture<?>[] longTermDB(GameContextDTO gameContextDTO) {
        LongTermFields longTermFields = connectToDatabase.getLongTermFields();
        Map<String, Integer> leaderboard = gameContextDTO.gameState().leaderboard();
        CompletableFuture<?>[] futures = new CompletableFuture[4];
        int index = 0;
        for (var player: leaderboard.entrySet()) {
            Bson filter = eq(longTermFields.getUsernameLabel(), player.getKey());
            FindPublisher<Document> publisher = (FindPublisher<Document>) this.collection.find(filter).first();
            Document doc = Flux.from(publisher).blockLast();
            
            /* Creates a new document if the player does not exist. Otherwise it updates datas. */
            if (doc.isEmpty()) {
                Document newDoc = new Document(longTermFields.getUsernameLabel(), player.getKey())
                            .append(longTermFields.getnMatchLabel(), 1)
                            .append(longTermFields.getnWinsLabel(), player.getKey().equals(gameContextDTO.gameState().winnerId()) ? 1 : 0)
                            .append(longTermFields.getBestScoreLabel(), player.getValue());
                     
                Publisher<InsertOneResult> insertPublisher = this.collection.insertOne(newDoc);
                futures[index] = Mono.from(insertPublisher).toFuture();
            } else {
                int bestScore = (int) doc.get(longTermFields.getBestScoreLabel());
                List<Bson> updates = new ArrayList<>();
                if (player.getValue() > bestScore) {
                    updates.add(set(longTermFields.getBestScoreLabel(), player.getValue()));
                }
                if (player.getKey().equals(gameContextDTO.gameState().winnerId())) {
                    updates.add(inc(longTermFields.getnWinsLabel(), 1));
                }
                updates.add(inc(longTermFields.getnMatchLabel(), 1));
                Bson updt = combine(updates);
                Publisher<UpdateResult> updateResult = this.collection.updateOne(filter, updt);
                futures[index] = Mono.from(updateResult).toFuture();
            }
            index++;
        }
        return futures;
    }

    /*
     * Closes the MongoDB connection.
     */
    public void closeConnection() {
        this.mongoClient.close();
    }

}
