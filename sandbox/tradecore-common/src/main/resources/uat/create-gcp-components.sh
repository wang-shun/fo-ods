#!/usr/bin/env bash

#Create Cluster
export PROJECT_ID=fo-ods
export ZONE=europe-west1-c
export CLUSTER_VERSION=1.7.5
export REGION=europe-west1


#STORE
export SERVICE=uat-xa-tradecore-store
export NUMPOD=3
gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION" --machine-type "n1-std-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"

export SERVICE=uat-xa-tradecore-service
export NUMPOD=3
gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION"  --machine-type "custom-1-5120" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"

read -p "Press [Enter] key to start deployment..."

#DB
#Spanner
export INSTANCE_ID=uat-xa-tradecore
export DATABASE_ID=uat-xa-tradecore
export NODES=2
export DESCRIPTION=UAT_XA_TradeCore_Database
echo "Creating Spanner Instance"
gcloud spanner instances create $INSTANCE_ID --config=$REGION --description=$DESCRIPTION --nodes=$NODES
gcloud spanner instances list --instance=$INSTANCE_ID
echo "Creating New Database"
gcloud spanner databases create $DATABASE_ID --instance=$INSTANCE_ID
gcloud spanner databases list --instance=$INSTANCE_ID
echo "Setting up Schema"
#gcloud spanner databases ddl update $DATABASE_ID --instance=$INSTANCE_ID --ddl='<script>'

read -p "Press [Enter] key to start deployment..."

#UI
export SERVICE=uat-eq-tradecore-vs
export NUMPOD=1
gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION" --machine-type "n1-std-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"

export SERVICE=uat-fx-tradecore-vs
export NUMPOD=1
gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION" --machine-type "n1-std-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"

export SERVICE=uat-ir-tradecore-vs
export NUMPOD=1
gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION" --machine-type "n1-std-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"


#ULT, LCM & DLT
export SERVICE=uat-eq-tradecore-service
export NUMPOD=8
gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION" --machine-type "n1-std-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"

export SERVICE=uat-fx-tradecore-service
export NUMPOD=8
gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION" --machine-type "n1-std-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"

export SERVICE=uat-ir-tradecore-service
export NUMPOD=8
gcloud beta container --project "$PROJECT_ID" clusters create "$SERVICE" --zone "$ZONE" --username="admin" --cluster-version "$CLUSTER_VERSION" --machine-type "n1-std-1" --image-type "COS" --disk-size "100" --scopes "https://www.googleapis.com/auth/userinfo.email","https://www.googleapis.com/auth/compute","https://www.googleapis.com/auth/devstorage.full_control","https://www.googleapis.com/auth/taskqueue","https://www.googleapis.com/auth/bigquery","https://www.googleapis.com/auth/sqlservice.admin","https://www.googleapis.com/auth/datastore","https://www.googleapis.com/auth/logging.admin","https://www.googleapis.com/auth/monitoring","https://www.googleapis.com/auth/cloud-platform","https://www.googleapis.com/auth/bigtable.data","https://www.googleapis.com/auth/bigtable.admin","https://www.googleapis.com/auth/pubsub","https://www.googleapis.com/auth/servicecontrol","https://www.googleapis.com/auth/service.management","https://www.googleapis.com/auth/trace.append" --num-nodes "$NUMPOD" --network "default" --enable-cloud-logging --enable-cloud-monitoring --enable-autoscaling --enable-legacy-authorization --min-nodes "1" --max-nodes "$NUMPOD"

read -p "Press [Enter] key to start pub/sub creation..."
## Create Topics & Subscriptions

#Equities
export TOPIC_NAME=uat-eq-ult-feed
export SUBSCRIPTION_NAME=uat-eq-ult-feed-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600

export TOPIC_NAME=uat-eq-dlt-feed
export SUBSCRIPTION_NAME=uat-eq-dlt-feed-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600

export TOPIC_NAME=uat-eq-dlt-err
export SUBSCRIPTION_NAME=uat-eq-dlt-err-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600

#FX
export TOPIC_NAME=uat-fx-ult-feed
export SUBSCRIPTION_NAME=uat-fx-ult-feed-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600

export TOPIC_NAME=uat-fx-dlt-feed
export SUBSCRIPTION_NAME=uat-fx-dlt-feed-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600

export TOPIC_NAME=uat-fx-dlt-err
export SUBSCRIPTION_NAME=uat-fx-dlt-err-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600


#IR
export TOPIC_NAME=uat-ir-ult-feed
export SUBSCRIPTION_NAME=uat-ir-ult-feed-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600

export TOPIC_NAME=uat-ir-dlt-feed
export SUBSCRIPTION_NAME=uat-ir-dlt-feed-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600

export TOPIC_NAME=uat-ir-dlt-err
export SUBSCRIPTION_NAME=uat-ir-dlt-err-sub
gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUBSCRIPTION_NAME --topic $TOPIC_NAME --ack-deadline 600



