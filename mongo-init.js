// Instruction the be done once the mongodb container is up.

// Returns the shortDB database.
db = db.getSiblingDB('shortDB');

// Creates the collection matches where to store the short-term datas.
db.createCollection("matches");