#!/usr/bin/env bash
export GOOGLE_APPLICATION_CREDENTIALS=src/main/resources/service-account/fo-ods-cred.json
kubectl delete deployment xa-tradecore-bridge
kubectl apply -f src/main/docker/xa-tradecore-bridge-deployment.yaml
