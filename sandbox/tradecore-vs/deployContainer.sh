#!/usr/bin/env bash
gcloud container clusters get-credentials sit-xa-tradecore-vs --zone europe-west1-c --project fo-ods
kubectl delete deployment sit-eq-tradecore-vs
kubectl delete deployment sit-ir-tradecore-vs
#kubectl delete deployment sit-fx-tradecore-vs
sleep 15s
kubectl create -f src/main/Docker/sit-eq-tradecore-vs-deploy.yaml
kubectl create -f src/main/Docker/sit-ir-tradecore-vs-deploy.yaml
#kubectl create -f src/main/Docker/sit-fx-tradecore-vs-deploy.yaml
sleep 10s
kubectl get deployments
