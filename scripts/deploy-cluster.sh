#!/bin/bash
echo "Deploying cluster on Minikube";
if [[ -d $1 ]]; then
  for directory in $(ls $1); do
    kubectl apply -f $1/$directory;
    sleep 1;
    kubectl apply -f $1/$directory;
  done
else
  echo "$1 is not a directory. Cannot perform the deployment operation.";
fi
