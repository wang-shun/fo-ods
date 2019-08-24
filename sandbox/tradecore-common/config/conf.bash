#!/bin/bash

alias kc=kubectl

kctx() {
    ctx=$1
    curctx=$(kubectl config current-context)
    if [ "x$ctx" == "x" ] ; then
        echo "Current context: $curctx"
    elif [ "$ctx" == "-" ] ; then
        kubectl config get-contexts
    else
        echo "Switching context from $curctx to $1"
        kubectl config use-context $1
    fi
}
