// Instruction the be done once the mongodb container is up.

// Returns the authDB database.
db = db.getSiblingDB('statsDB');

// Creates the collection auth where to store the data of the auth server.
db.createCollection("stats");