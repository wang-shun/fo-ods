#!/bin/bash

#brew install gettext
#brew link --force gettext

export REPLICAS=2
export TAG=roman

export VARIANT=uat-fxd-vs
export SUBSCRIPTION=uat-fxd-lcm-feed-vs-sub

output=$(kc-apply-template.sh gke/demo-drain-template.yaml)
kc-execute.sh $output $*
