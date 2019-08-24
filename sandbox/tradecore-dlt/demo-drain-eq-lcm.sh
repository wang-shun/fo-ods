#!/bin/bash

#brew install gettext
#brew link --force gettext

export REPLICAS=2
export TAG=roman

export VARIANT=uat-eqt-lcm
export SUBSCRIPTION=uat-eqt-ult-feed-lcm-sub

output=$(kc-apply-template.sh gke/demo-drain-template.yaml)
kc-execute.sh $output $*
