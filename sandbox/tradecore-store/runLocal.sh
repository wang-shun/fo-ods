#!/usr/bin/env bash

export TC_STORE_SRVC_PORT=5555
export NO_OF_THREADS=3
export INSTANCE_ID=tradecore
export DATABASE=tradecore

java -jar target/tradecore-store-0.1.0-SNAPSHOT.jar