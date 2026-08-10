# distributed-multiplayer-pacman

## Deploying the system on Minikube

This section contains the instructions for deploying the system on a Minikube local cluster.

### Installing Minikube with Agones

The prerequisite for deploying the system is to have a cluster with the Kubernetes framework "Agones".
For Minikube, this involves the following steps:

**Creating a Minikube cluster compatible with Agones:**

Linux and Mac:

`minikube start --kubernetes-version v1.35 -p agones --memory 6000 --cpus 4`

Windows:

It's necessary to publish the desired Agones port range from Minikube to the host system.
So, the command becomes:

`minikube start --kubernetes-version v1.35 -p agones --ports 7000-7050:7000-7050/tcp --ports 7000-7050:7000-7050/udp --memory 6000 --cpus 4`

We will publish both the TCP and UDP port range, since our GameServer communicates using both protocols.

After the first creation of Minikube's custom profile, we can simply start it with:

`minikube start -p agones`

**Installing Agones on our Minikube cluster:**

Once the Minikube cluster is running, we can install Agones in the cluster by using Helm (installed in our host machine).
To do so, we will run the following commands in a terminal on our host:

`helm repo add agones https://agones.dev/chart/stable`

`helm repo update`

`helm install my-release --namespace agones-system --create-namespace --set gameservers.minPort=7000,gameservers.maxPort=7050 agones/agones`

It's imperative to make sure that the port range specified in this command corresponds to the one specified during the
creation of Minikube's profile.

**Setting up an Ingress controller in our cluster**

TODO: INGRESS RESOURCE IS DEPRECATED, A GATEWAY IS BETTER

The following instructions were taken from the following guide: https://v1-32.docs.kubernetes.io/docs/tasks/access-application-cluster/ingress-minikube/

The correct functioning of the system requires enabling an Ingress controller inside the Minikube cluster.
One way to do this is by using the NGINX Ingress controller, which can be enabled by executing the following command
once the cluster is running:

`minikube -p agones addons enable ingress`

After running this command, we can verify that the NGINX controller is running with the following command:

`kubectl get pods -n ingress-nginx`

**Setting up a Gateway controller in our cluster**

TODO: WRITE AFTER FINDING A CONTROLLER THAT WORKS

### Deploying the system in the cluster

TODO: WRITE THIS SECTION