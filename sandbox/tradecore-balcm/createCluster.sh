#!/usr/bin/env bash
export PROJECT_ID=fo-ods
export SERVICE=sit-xa-tradecore-balcm
export ZONE=europe-west1-c
export NUMPOD=3
export CLUSTER_VERSION=1.7.5

gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION" --machine-type "n1-std-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"