#!/bin/bash

input=$1
action=$2

if [ "x$action" == 'x' -o "x$action" == 'xstop' ] ; then
    echo "Deleting...."
    kubectl delete -f $input
fi


if [ "x$action" == 'x' -o "x$action" == 'xstart' ] ; then
    echo "Starting...."
    kubectl create -f $input
fi
