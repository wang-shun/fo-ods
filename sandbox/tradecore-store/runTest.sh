#!/usr/bin/env bash
gcloud container clusters get-credentials dev-xa-tradecore-store-test-client --zone europe-west1-c --project fo-ods
kubectl delete job sit-xa-tradecore-store-single-zone-client-job
sleep 15s
kubectl create -f src/main/docker/sit-xa-tradecore-store-client-single-zone-job.yaml
sleep 10s
kubectl get jobs

