echo "Cleaning up cluster"
$kubepath = $args[0]
if (Test-Path -Path $kubepath) {
    foreach ($folder in $(dir $kubepath)) {
        kubectl delete -f $kubepath\$folder
    }
    kubectl delete pvc --all --all-namespaces
} else {
    echo "$kubepath is not a directory. Cannot perform the cleanup operation."
}
