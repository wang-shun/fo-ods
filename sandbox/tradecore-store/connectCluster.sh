#!/usr/bin/env bash
gcloud container clusters get-credentials sit-xa-tradecore-store --zone europe-west1-c --project fo-ods
kubectl proxy
