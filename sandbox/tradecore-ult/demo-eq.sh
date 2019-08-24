#!/bin/bash

export TAG=roman

export VARIANT=uat-eqt
export TOPIC=uat-eqt-ult-feed
export TRADE_COUNT=100
export TRADE_TYPE=cash-eq
export LEGS_COUNT=1

output=$(kc-apply-template.sh gke/ult-template.pod.yaml)
kc-execute.sh $output $*
