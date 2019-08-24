#!/usr/bin/env bash
echo ------ building the project ----------
mvn clean install -DskipTests
cp src/main/docker/Dockerfile .
echo ------ building the docker image ----------
#docker build -t gcr.io/fo-ods/tradecore-md .
echo ------ uploading the docker image ----------
#gcloud docker -- push gcr.io/fo-ods/tradecore-md
gcloud container builds submit --tag gcr.io/fo-ods/tradecore-md .
rm Dockerfile
echo ------ done ----------