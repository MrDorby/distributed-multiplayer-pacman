echo "Deploying cluster on Minikube"
$kubepath = $args[0]
if (Test-Path -Path $kubepath) {
    foreach ($folder in $(dir $kubepath)) {
        kubectl apply -f $kubepath\$folder
        Start-Sleep -Seconds 1
        kubectl apply -f $kubepath\$folder
    }
} else {
    echo "$kubepath is not a directory. Cannot perform the deployment operation."
}
