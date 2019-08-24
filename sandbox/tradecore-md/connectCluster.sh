#!/usr/bin/env bash
gcloud container clusters get-credentials sit-xa-tradecore-vs --zone europe-west1-c --project fo-ods
kubectl proxy
