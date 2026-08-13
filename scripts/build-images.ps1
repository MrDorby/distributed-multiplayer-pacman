echo "Working dir: $(pwd)"
echo "Specified projects: $args"
minikube status -p agones
if ( -not $? ) {
    echo "Minikube is not started. Starting Minikube with `"Agones`" profile..."
    minikube start -p agones;
}
& minikube -p agones docker-env --shell powershell | Invoke-Expression
echo "Building project images on Minikube cluster... "
foreach ($project in $args) {
    echo "Building image for $project..."
    docker build -t "$project" $project
}
