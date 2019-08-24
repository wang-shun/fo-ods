#!/bin/bash

input=$1
export output=/tmp/$(basename $input)

envsubst < $input > $output
echo $output
