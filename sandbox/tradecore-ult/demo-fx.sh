#!/bin/bash

export TAG=roman

export VARIANT=uat-fxd
export TOPIC=uat-fxd-ult-feed
export TRADE_COUNT=1000
export TRADE_TYPE=fx-fwd
export LEGS_COUNT=2

output=$(kc-apply-template.sh gke/ult-template.pod.yaml)
kc-execute.sh $output $*
