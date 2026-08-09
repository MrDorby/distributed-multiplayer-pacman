echo "Specified projects: %*"
minikube status -p agones
if ERRORLEVEL 1 (
    echo "Minikube is not started. Starting Minikube with \"Agones\" profile..."
    minikube start -p agones
)
@FOR /f "tokens=*" %%i IN ('minikube -p agones docker-env --shell cmd') DO @%%i
for %%p in (%*) do (
    echo "Building image for %%p..."
::    docker build -t "%%p" ./%%p
)
:: TODO: NON FUNZIONA