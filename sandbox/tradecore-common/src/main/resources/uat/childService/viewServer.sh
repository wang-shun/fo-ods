#!/usr/bin/env bash
export PROJECT_ID=fo-ods
export ZONE=europe-west1-c
export CLUSTER_VERSION=1.7.5

### View Server

#Deployment

export SERVICE=uat-eq-tradecore-vs
gcloud container clusters get-credentials $SERVICE --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f ../$SERVICE-deploy.yaml
kubectl get deployments

export SERVICE=uat-fx-tradecore-vs
gcloud container clusters get-credentials $SERVICE --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f ../$SERVICE-deploy.yaml
kubectl get deployments

export SERVICE=uat-ir-tradecore-vs
gcloud container clusters get-credentials $SERVICE --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f ../$SERVICE-deploy.yaml
kubectl get deployments