#!/bin/bash
echo "Working dir: `pwd`"
echo "Specified projects: $@"
if ! minikube status -p agones; then
  echo "Minikube is not started. Starting Minikube with \"Agones\" profile..."
  minikube start -p agones;
fi;
eval $(minikube -p agones docker-env --shell bash);
echo "Building project images on Minikube cluster..."
for project in "$@"; do
  echo "Building image for ${project}..."
  docker build -t "${project}" ./${project};
done;
