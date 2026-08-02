#!/bin/bash

# Name: PodName.ServiceName.Namespace.svc.cluster.local
m1=authdb-0.auth-mongo.authdb.svc.cluster.local
m2=authdb-1.auth-mongo.authdb.svc.cluster.local
m3=authdb-2.auth-mongo.authdb.svc.cluster.local
port=27017

echo "###### Waiting for ${m1} instance startup.."
until mongosh --host ${m1}:${port} --eval 'quit(db.runCommand({ ping: 1 }).ok ? 0 : 2)' &>/dev/null; do
  printf '.'
  sleep 1
done
echo "###### Working ${m1} instance found, initiating user setup & initializing rs setup.."

mongo admin --eval <<END
db.createUser({
    user:'$MONGO_INITDB_ROOT_USERNAME',
    pwd:'$MONGO_INITDB_ROOT_PASSWORD',
    roles:[{role:'root',db:'admin'}]
    })
END

# setup user + pass and initialize replica sets
mongosh --host ${m1}:${port} <<EOF
var rootUser = '$MONGO_INITDB_ROOT_USERNAME';
var rootPassword = '$MONGO_INITDB_ROOT_PASSWORD';
var admin = db.getSiblingDB('admin');
admin.auth(rootUser, rootPassword);

var config = {
    "_id": "replicaSet",
    "version": 1,
    "members": [
        {
            "_id": 0,
            "host": "${m1}:${port}",
            "priority": 2
        },
        {
            "_id": 1,
            "host": "${m2}:${port}",
            "priority": 1
        },
        {
            "_id": 2,
            "host": "${m3}:${port}",
            "priority": 1,
        }
    ]
};
rs.initiate(config, { force: true });
rs.status();
EOF