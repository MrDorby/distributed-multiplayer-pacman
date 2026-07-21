package it.unibo.controller.server.persistence.mongodb;

/**
 * MongoDB constants useful for the game server to connect with the different database instances.
 */
public class MongoDBConstants {

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

    /* Label of the different fields in each database. */
    private static final LongTermFields longTermFields = new LongTermFields("username", "nMatch", "nWins", "bestScore");
    private static final ShortTermFields shortTermFields = new ShortTermFields("matchId", "checkpoints");;

    private MongoDBConstants() {}

    public enum ConnectToDatabase {
    
        /** Long Term MongoDB database informations. */
        LONG_TERM(LT, LT_DB, LT_CL),

        /** Short Term MongoDB database informations. */
        SHORT_TERM(ST, ST_DB, ST_CL);

        private final String connectionString;
        private final String databaseName;
        private final String collectionName;
        private final LongTermFields lTermFields;
        private final ShortTermFields sTermFields;

        ConnectToDatabase(String connectionString, String databaseName, String collectionName) {
            this.connectionString = connectionString;
            this.databaseName = databaseName;
            this.collectionName = collectionName;
            this.lTermFields = longTermFields;
            this.sTermFields = shortTermFields;
        }

        public String getConnectionString() {
            return connectionString;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public String getCollectionName() {
            return collectionName;
        }

        public LongTermFields getLongTermFields() {
            return lTermFields;
        }

        public ShortTermFields getShortTermFields() {
            return sTermFields;
        }

    }

    /*  */
    //private static interface Fields {}

    /**
     * String labels of the long-term database's fields.
      */
    public static class LongTermFields {
        
        private String username;
        private String nMatch;
        private String nWins;
        private String bestScore;

        public LongTermFields(String username, String nMatch, String nWins, String bestScore) {
            this.username = username;
            this.nMatch = nMatch;
            this.nWins = nWins;
            this.bestScore = bestScore;
        }

        public String getUsernameLabel() {
            return username;
        }

        public String getnMatchLabel() {
            return nMatch;
        }

        public String getnWinsLabel() {
            return nWins;
        }

        public String getBestScoreLabel() {
            return bestScore;
        }
    }


    /**
     * String labels of the short-term database's fields.
      */
    public static class ShortTermFields {
        
        private final String matchId;
        // TODO: add list user.
        private final String checkpoints;
        
        public ShortTermFields(String matchId, String checkpoints) {
            this.matchId = matchId;
            this.checkpoints = checkpoints;
        }

        public String getMatchIdLabel() {
            return matchId;
        }

        public String getCheckpointsLabel() {
            return checkpoints;
        }
    }
}
