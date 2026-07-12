// Instruction the be done once the mongodb container is up.

try {
    rs.status();
    
} catch (e) {
    rs.initiate({
        _id: "replicaSet",
        members: [
            { _id: 0, host: "authdb1:27017" },
            { _id: 1, host: "authdb2:27017" }
        ]
    });
}

// rs.initiate({
//     _id: "replicaSet", 
//     members: [
//           { _id: 0, host: "authdb-1:27017"},
//           { _id: 1, host: "authdb-2:27017"}
//         ]
//     });
// Returns the authDB database.
db = db.getSiblingDB('authDB');

// Creates the collection auth where to store the data of the auth server.
db.createCollection("auth");