echo "Cleaning up cluster"
$kubepath = $args[0]
if (Test-Path -Path $kubepath) {
    foreach ($folder in $(dir $kubepath)) {
        kubectl delete -f $kubepath\$folder
    }
    kubectl delete pvc --all --all-namespaces
    kubectl delete pods $(kubectl get pods -o name | Select-String "pacman-server")
    kubectl delete $(kubectl get gs -o name | Select-String "pacman-server")
} else {
    echo "$kubepath is not a directory. Cannot perform the cleanup operation."
}
