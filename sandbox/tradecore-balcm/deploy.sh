#!/usr/bin/env bash

gcloud container clusters get-credentials sit-xa-tradecore-balcm --zone europe-west1-c --project fo-ods

kubectl delete deployment sit-eq-tradecore-balcm
#kubectl delete deployment sit-fx-tradecore-balcm
kubectl delete deployment sit-ir-tradecore-balcm
sleep 15s
kubectl create -f src/main/Docker/sit-eq-tradecore-balcm-deploy.yaml
#kubectl create -f src/main/Docker/sit-fx-tradecore-balcm-deploy.yaml
kubectl create -f src/main/Docker/sit-ir-tradecore-balcm-deploy.yaml
sleep 10s
kubectl get deployments
read -p "Press [Enter] key to continue to create service..."
echo "Are you really sure that you want to create service?"
read -p "Press [Enter] key to confirm again.."
kubectl create -f src/main/docker/sit-ir-tradecore-balcm-service.yaml
kubectl create -f src/main/docker/sit-eq-tradecore-balcm-service.yaml
#kubectl create -f src/main/docker/sit-fx-tradecore-balcm-service.yaml
kubectl get services
read -p "Press [Enter] key to connect to cluster..."
kubectl proxy