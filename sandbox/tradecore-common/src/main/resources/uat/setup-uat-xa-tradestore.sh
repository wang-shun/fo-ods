#!/usr/bin/env bash

export PROJECT_ID=fo-ods
export ZONE=europe-west1-c
export CLUSTER_VERSION=1.7.5


###STORE

#Deployment
export SERVICE=uat-xa-tradecore-store
gcloud container clusters get-credentials $SERVICE --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments
#Service - Store
read -p "Press [Enter] key to create service..."
kubectl delete service $SERVICE
kubectl create -f $SERVICE-service.yaml
kubectl get services

export ASSET=uat-xa-tradecore
export CLUSTER=uat-xa-tradecore-service
export SERVICE=$ASSET-ult
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl create -f $SERVICE-service.yaml
kubectl get deployments

