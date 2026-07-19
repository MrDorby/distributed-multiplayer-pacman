#!/bin/bash

mongosh -u $MONGO_INITDB_ROOT_USERNAME -p $MONGO_INITDB_ROOT_PASSWORD --eval "
db.getSiblingDB('statsDB').createCollection('stats');
"