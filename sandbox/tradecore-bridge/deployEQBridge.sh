#!/usr/bin/env bash

gcloud container clusters get-credentials sit-xa-tradecore-bridge --zone europe-west1-c --project fo-ods

kubectl delete deployment sit-eq-tradecore-md-62-bridge
kubectl delete deployment sit-eq-tradecore-md-74-bridge
kubectl delete deployment sit-eq-tradecore-md-217-bridge
kubectl delete deployment sit-eq-tradecore-md-2989-bridge
kubectl delete deployment sit-eq-tradecore-md-4022-bridge
kubectl delete deployment sit-eq-tradecore-md-4741-bridge
kubectl delete deployment sit-eq-tradecore-md-4945-bridge
kubectl delete deployment sit-eq-tradecore-md-5625-bridge
kubectl delete deployment sit-eq-tradecore-md-5926-bridge
kubectl delete deployment sit-eq-tradecore-md-8132-bridge
read -p "Press [Enter] key to continue to create deployments..."
sleep 10s
kubectl delete secret pubsub-auth-key
kubectl create secret generic pubsub-auth-key --from-file=key.json=/Users/madhav/java/fo-ods/sandbox/tradecore-common/src/main/resources/cred/md/fo-ods-cred.json
kubectl apply -f src/main/docker/sit-eq-tradecore-md-62-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-74-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-217-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-2989-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-4022-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-4741-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-4945-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-5625-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-5926-bridge-deploy.yaml
kubectl apply -f src/main/docker/sit-eq-tradecore-md-8132-bridge-deploy.yaml
sleep 10s
kubectl get deployments
