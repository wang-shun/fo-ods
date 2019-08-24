#!/usr/bin/env bash

export APP_ID=fo-ods
export LCM_TOPIC_NAME=uat-ir-lcm-feed
export DLT_ERR_TOPIC_NAME=uat-ir-dlt-err
export DLT_SUB_NAME=uat-ir-lcm-feed-dlt-sub
export VS_SUB_NAME=uat-ir-lcm-feed-vs-sub
export DIST_SUB_NAME=uat-ir-lcm-feed-dist-sub
export DLT_ERR_SUB_NAME=uat-ir-dlt-err-sub

echo on

gcloud beta pubsub topics delete $LCM_TOPIC_NAME
gcloud beta pubsub subscriptions delete $DLT_SUB_NAME
gcloud beta pubsub subscriptions delete $VS_SUB_NAME
gcloud beta pubsub subscriptions delete $DIST_SUB_NAME
gcloud beta pubsub subscriptions delete $DLT_ERR_SUB_NAME

gcloud beta pubsub topics create $LCM_TOPIC_NAME
gcloud beta pubsub subscriptions create $DLT_SUB_NAME --topic $LCM_TOPIC_NAME --ack-deadline 600
gcloud beta pubsub subscriptions create $VS_SUB_NAME --topic $LCM_TOPIC_NAME --ack-deadline 600
gcloud beta pubsub subscriptions create $DIST_SUB_NAME --topic $LCM_TOPIC_NAME --ack-deadline 600
gcloud beta pubsub subscriptions create $DLT_ERR_SUB_NAME --topic $DLT_ERR_TOPIC_NAME --ack-deadline 600
