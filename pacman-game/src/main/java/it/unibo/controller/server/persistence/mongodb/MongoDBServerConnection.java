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

import it.unibo.controller.shared.network.dto.GameContextDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MongoDBServerConnection manages the GameServer connections with the MongoDB instances.
 */
public class MongoDBServerConnection {
    
    private record DatabaseInfo(String stringConnection, String database, String collection) {
    }

    // TODO: Is it ok to insert Connection String with password?
    /* Connection String for a specific MongoDB database. */
    private static final String LT = "mongodb://admin:password@queriesdb1:27017,queriesdb2:27017,queriesdb3:27017/authDB?authSource=admin&replicaSet=replicaSet";
    private static final String ST = "";
    
    /* MongoDB Databases. */
    private static final String LT_DB = "statsDB";
    private static final String ST_DB = "";

    /* MongoDB Colletions. */
    private static final String LT_CL = "stats";
    private static final String ST_CL = "";

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;
    private final MongoCollection<Document> collection;
    private final Map<ConnectToDatabase, DatabaseInfo> connections =  Map.of(
        ConnectToDatabase.LONG_TERM, new DatabaseInfo(LT, LT_DB, LT_CL),
        ConnectToDatabase.SHORT_TERM, new DatabaseInfo(ST, ST_DB, ST_CL)
    );

    private ConnectToDatabase connectToDatabase;

    public MongoDBServerConnection(ConnectToDatabase connectionString) {
        this.connectToDatabase = connectionString;
        DatabaseInfo info = connections.get(connectionString);
        this.mongoClient = MongoClients.create(info.stringConnection());
        this.mongoDatabase = this.mongoClient.getDatabase(info.database());
        this.collection = this.mongoDatabase.getCollection(info.collection());
    }

    /**
     * This methods handles database save requests, for both long-term and short-term databases.
     * @param gameContextDTO containing the datas that need to be stored.
     * @return a CompletableFuture of the action done.
     */
    public CompletableFuture<Void> saveResultsOnDB(GameContextDTO gameContextDTO) {
        if (this.connectToDatabase.equals(ConnectToDatabase.LONG_TERM)) {
            return longTermDB(gameContextDTO);
        } else {
            return null; //TODO: Complete for the short term db.
        }
        
    }

    /* Handles the requests for the long-term database. */
    private CompletableFuture<Void> longTermDB(GameContextDTO gameContextDTO) {
        Map<String, Integer> leaderboard = gameContextDTO.gameState().leaderboard();
        CompletableFuture<?>[] futures = new CompletableFuture[4];
        int index = 0;
        for (var player: leaderboard.entrySet()) {
            Bson filter = eq("username", player.getKey());
            FindPublisher<Document> publisher = (FindPublisher<Document>) this.collection.find(filter).first();
            Document doc = Flux.from(publisher).blockLast();
            if (doc.isEmpty()) {
                Document newDoc = new Document("username", player.getKey())
                            .append("nMatch", 1)
                            .append("nWins", player.getKey().equals(gameContextDTO.gameState().winnerId()) ? 1 : 0)
                            .append("bestScore", player.getValue());
                            
                Publisher<InsertOneResult> insertPublisher = this.collection.insertOne(newDoc);
                futures[index] = Mono.from(insertPublisher).toFuture();
            } else {
                int bestScore = (int) doc.get("bestScore");
                List<Bson> updates = new ArrayList<>();
                if (player.getValue() > bestScore) {
                    updates.add(set("bestScore", player.getValue()));
                }
                if (player.getKey().equals(gameContextDTO.gameState().winnerId())) {
                    updates.add(inc("nWins", 1));
                }
                updates.add(inc("nMatch", 1));
                Bson updt = combine(updates);
                Publisher<UpdateResult> updateResult = this.collection.updateOne(filter, updt);
                futures[index] = Mono.from(updateResult).toFuture();
            }
            index++;
        }
        return CompletableFuture.allOf(futures);
    }

    /**
     * Closes the MongoDB connection.
     */
    public void closeConnection() {
        this.mongoClient.close();
    }

}
