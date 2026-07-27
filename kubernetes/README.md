# Kubernetes deployment files

This directory contains the files used to deploy the system on a Kubernetes cluster.

## Deploying the system on Minikube

This section contains the instructions for deploying the system on a Minikube local cluster.

### Installing Minikube with Agones

The prerequisite for deploying the system is to have a cluster with the Kubernetes framework "Agones".
For Minikube, this involves the following steps:

**Creating a Minikube cluster compatible with Agones:**

Linux and Mac:

`minikube start --kubernetes-version v1.35 -p agones`

Windows:

It's necessary to publish the desired Agones port range from Minikube to the host system.
So, the command becomes:

`minikube start --kubernetes-version v1.35 -p agones --ports 7000-7050:7000-7050/tcp --ports 7000-7050:7000-7050/udp`

We will publish both the TCP and UDP port range, since our GameServer communicates using both protocols.

After the first creation of Minikube's custom profile, we can simply start it with:

`minikube start -p agones`

**Installing Agones on our Minikube cluster:**

Once the Minikube cluster is running, we can install Agones in the cluster by using Helm (installed in our host machine).
To do so, we will run the following command in a terminal on our host:

`helm install my-release --namespace agones-system --create-namespace --set gameservers.minPort=7000,gameservers.maxPort=7050 agones/agones`

It's imperative to make sure that the port range specified in this command corresponds to the one specified during the
creation of Minikube's profile.

### Deploying the system in the cluster

TODO: WRITE THIS SECTION