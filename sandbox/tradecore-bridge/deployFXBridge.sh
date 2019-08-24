#!/usr/bin/env bash
gcloud container clusters get-credentials sit-xa-tradecore-bridge --zone europe-west1-c --project fo-ods
kubectl delete deployment sit-fx-tradecore-md-213-bridge
read -p "Press [Enter] key to continue to create deployments..."
sleep 10s
kubectl delete secret pubsub-auth-key
kubectl create secret generic pubsub-auth-key --from-file=key.json=/Users/madhav/java/fo-ods/sandbox/tradecore-common/src/main/resources/cred/md/fo-ods-cred.json
kubectl apply -f src/main/docker/sit-fx-tradecore-md-213-bridge-deploy.yaml
sleep 10s
kubectl get deployments
