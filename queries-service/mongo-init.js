// Instruction the be done once the mongodb container is up.

// Returns the statsDB database.
db = db.getSiblingDB('statsDB');

// Creates the collection stats where to store the long-term datas.
db.createCollection("stats");