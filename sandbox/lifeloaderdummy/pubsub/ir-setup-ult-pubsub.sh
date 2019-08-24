#!/usr/bin/env bash
#ird
export TOPIC_NAME=sit-ir-ult-feed
export SUB_NAME=sit-ir-ult-feed-lcm-sub
export APP_ID=fo-ods


gcloud beta pubsub topics delete $TOPIC_NAME
gcloud beta pubsub subscriptions delete $SUB_NAME


gcloud beta pubsub topics create $TOPIC_NAME
gcloud beta pubsub subscriptions create $SUB_NAME --topic $TOPIC_NAME --ack-deadline 600
