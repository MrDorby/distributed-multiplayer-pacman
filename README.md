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

**Installing necessary cluster dependencies:**

Once the Minikube cluster is running, we can install the necessary cluster extensions by using Helm (installed in our host machine).

First of all, we must add and update the Helm repositories that contain the extensions.
This can be done by running the following commands in a terminal on our host:

`helm repo add mongodb https://mongodb.github.io/helm-charts`

`helm repo add agones https://agones.dev/chart/stable`

`helm repo update`

The first requirement is Agones. We can install it on our cluster with the following command:

`helm install agones-release --version 1.59.0 --namespace agones-system --create-namespace --set gameservers.minPort=7000,gameservers.maxPort=7050 agones/agones`

It's imperative to make sure that the port range specified in this command corresponds to the one specified during the
creation of Minikube's profile.

Another requirement is MongoDB. We can install the necessary operators with the following commands:

`kubectl create namespace authdb`

`kubectl create namespace longdb`

`kubectl create namespace shortdb`

`helm install mongodb-operator mongodb/mongodb-kubernetes --namespace mongo-operator --create-namespace --set operator.watchNamespace="authdb\,longdb\,shortdb"`

### Deploying the system in the cluster

**Building the projects**

In the root directory of the project, execute Gradle's `build` task. This will build the jar files for every subproject.

**Building the docker images**

After building the jars, we will need to add the corresponding Docker images to Minikube's docker registry.
This can be done by running the provided script for the specific platform from the root directory of the project.
The command line arguments to execute the script are as follows:
- Linux: `./scripts/build-images.sh authenticator-service game-server-manager matchmaker pacman-game queries-service front-end`
- Windows (PowerShell): `.\scripts\build-images.ps1 authenticator-service game-server-manager matchmaker pacman-game queries-service front-end`

**Deploying the cluster**

The deployment of the cluster can be performed by running the provided script for the specific platform from the root
directory of the project.
The command lines to execute the script are as follows:
- Linux: `./scripts/deploy-cluster.sh kubernetes/`
- Windows (PowerShell): `.\scripts\deploy-cluster.ps1 .\kubernetes\`

*NOTE: After running this script, wait **at least 3 minutes** in order to allow the cluster to correcty complete the setup
    operations.*

**Exposing the front end service**

Once the cluster is running, we can expose the front end service via the following command:

`minikube tunnel -p agones`

The terminal in which we run this command must be kept open, so that the tunnel stays active.
Also, to make sure that the client is able to access the front end service, we will need to add the following line to
our system's /etc/hosts file:

`<cluster ip> multiplayer-pacman.unibo.it`

If we are testing the client in the same host as the cluster, the line will be:

`127.0.0.1 multiplayer-pacman.unibo.it`

**Playing the game**

Finally, we can play the game by executing the `pacman-game-1.0-full-client.jar` application.

**Cluster cleanup**

To stop the cluster, we will first have to stop Minikube's tunnel, by stopping the corresponding process
in the active terminal.

After that, we can remove all resources from our Minikube cluster.
This can be done in a similar way as the deployment operation:
- Linux: `./scripts/cleanup-cluster.sh kubernetes/`
- Windows (PowerShell): `.\scripts\cleanup-cluster.ps1 .\kubernetes\`

Finally, we can stop the cluster with the following command:

`minikube stop -p agones`

We can also remove every trace of the cluster's container with the following command:

`minikube delete -p agones`
