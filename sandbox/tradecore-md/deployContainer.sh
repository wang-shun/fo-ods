#!/usr/bin/env bash
gcloud container clusters get-credentials sit-xa-tradecore-vs --zone europe-west1-c --project fo-ods
kubectl delete deployment sit-fx-tradecore-md
kubectl delete deployment sit-eq-tradecore-md
sleep 10s
kubectl create -f src/main/docker/sit-fx-tradecore-md-deploy.yaml
kubectl create -f src/main/docker/sit-eq-tradecore-md-deploy.yaml
sleep 10s
kubectl get deployments
