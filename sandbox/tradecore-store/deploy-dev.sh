#!/usr/bin/env bash
gcloud container clusters get-credentials sit-xa-tradecore-balcm --zone europe-west1-c --project fo-ods
kubectl delete deployment dev-xa-tradecore-store
sleep 15s
kubectl create -f src/main/docker/dev-xa-tradecore-store-deploy.yaml
sleep 10s
kubectl get deployments
read -p "Press [Enter] key to continue to create service..."
echo "Are you really sure that you want to create service?"
read -p "Press [Enter] key to confirm again.."
kubectl create -f src/main/docker/dev-xa-tradecore-store-service.yaml
kubectl get services
