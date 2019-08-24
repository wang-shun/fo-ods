#!/bin/bash

# Sets up convenient shortcuts for cluster management.
# Can be added to the bash profile.
#     Usage : source setup.bash

SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ]; do # resolve $SOURCE until the file is no longer a symlink
  DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
  SOURCE="$(readlink "$SOURCE")"
  [[ $SOURCE != /* ]] && SOURCE="$DIR/$SOURCE" # if $SOURCE was a relative symlink, we need to resolve it relative to the path where the symlink file was located
done
DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"
echo DIR=$DIR

tradecoreConfig=$DIR/kube.config

export KUBECONFIG=$KUBECONFIG:$tradecoreConfig
echo KUBECONFIG=$KUBECONFIG

export PATH=$PATH:$DIR

source $DIR/conf.bash

