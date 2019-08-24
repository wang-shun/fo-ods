    #!/usr/bin/env bash
    gcloud container clusters get-credentials sit-xa-tradecore-dist-bridge --zone europe-west1-c --project fo-ods
    kubectl delete deployment sit-ir-tradecore-dist-bridge
    kubectl delete deployment sit-eq-tradecore-dist-bridge
    kubectl delete deployment sit-fx-tradecore-dist-bridge
    sleep 5s
    kubectl delete secret dist-pubsub-auth-key
    kubectl create secret generic dist-pubsub-auth-key --from-file=dist-key.json=tradecore-common/src/main/resources/cred/std/fo-ods-cred.json
    read -p "Press [Enter] key to continue to create deployments..."
    kubectl apply -f src/main/docker/sit-ir-tradecore-dist-bridge-deploy.yaml
    kubectl apply -f src/main/docker/sit-eq-tradecore-dist-bridge-deploy.yaml
    kubectl apply -f src/main/docker/sit-fx-tradecore-dist-bridge-deploy.yaml
    sleep 10s
    kubectl get deployments
