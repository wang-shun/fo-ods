#!/usr/bin/env bash
#Create Cluster
export PROJECT_ID=fo-ods
export ZONE=europe-west1-c
export CLUSTER_VERSION=1.7.5

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



