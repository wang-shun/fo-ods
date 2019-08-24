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
kubectl get deployments
kubectl create -f $SERVICE-service.yaml
kubectl get services


read -p "Press [Enter] key to start view server deployment..."
### View Server

#Deployment

export SERVICE=uat-eq-tradecore-vs
gcloud container clusters get-credentials $SERVICE --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export SERVICE=uat-fx-tradecore-vs
gcloud container clusters get-credentials $SERVICE --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export SERVICE=uat-ir-tradecore-vs
gcloud container clusters get-credentials $SERVICE --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export SERVICE=uat-fx-tradecore-md
gcloud container clusters get-credentials $SERVICE --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

read -p "Press [Enter] key to start ULT/LCM/DLT creation..."
###ULT, LCM & DLT

export ASSET=uat-eq-tradecore
export CLUSTER=uat-eq-tradecore-service
export SERVICE=$ASSET-lcm
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-eq-tradecore
export CLUSTER=uat-eq-tradecore-service
export SERVICE=$ASSET-balcm
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-eq-tradecore
export CLUSTER=uat-eq-tradecore-service
export SERVICE=$ASSET-dlt
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-eq-tradecore
export CLUSTER=uat-eq-tradecore-service
export SERVICE=$ASSET-err
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments


#Deployment


export ASSET=uat-fx-tradecore
export CLUSTER=uat-fx-tradecore-service
export SERVICE=$ASSET-lcm
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-fx-tradecore
export CLUSTER=uat-fx-tradecore-service
export SERVICE=$ASSET-balcm
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-fx-tradecore
export CLUSTER=uat-fx-tradecore-service
export SERVICE=$ASSET-dlt
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-fx-tradecore
export CLUSTER=uat-fx-tradecore-service
export SERVICE=$ASSET-err
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

#Deployment

export ASSET=uat-ir-tradecore
export CLUSTER=uat-ir-tradecore-service
export SERVICE=$ASSET-lcm
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-ir-tradecore
export CLUSTER=uat-ir-tradecore-service
export SERVICE=$ASSET-balcm
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-ir-tradecore
export CLUSTER=uat-ir-tradecore-service
export SERVICE=$ASSET-dlt
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

export ASSET=uat-ir-tradecore
export CLUSTER=uat-ir-tradecore-service
export SERVICE=$ASSET-err
gcloud container clusters get-credentials $CLUSTER --zone $ZONE --project $PROJECT_ID
kubectl delete deployment $SERVICE
read -p "Press [Enter] key to start deployment..."
kubectl create -f $SERVICE-deploy.yaml
kubectl get deployments

#Service - LCM
export SERVICE=uat-eq-tradecore-lcm
read -p "Press [Enter] key to create service..."
kubectl delete service $SERVICE
kubectl create -f $SERVICE-service.yaml
kubectl get services

export SERVICE=uat-fx-tradecore-lcm
read -p "Press [Enter] key to create service..."
kubectl delete service $SERVICE
kubectl create -f $SERVICE-service.yaml
kubectl get services

export SERVICE=uat-ir-tradecore-lcm
read -p "Press [Enter] key to create service..."
kubectl delete service $SERVICE
kubectl create -f $SERVICE-service.yaml
kubectl get services

#Service - BALCM
export SERVICE=uat-eq-tradecore-balcm
read -p "Press [Enter] key to create service..."
kubectl delete service $SERVICE
kubectl create -f $SERVICE-service.yaml
kubectl get services

export SERVICE=uat-fx-tradecore-balcm
read -p "Press [Enter] key to create service..."
kubectl delete service $SERVICE
kubectl create -f $SERVICE-service.yaml
kubectl get services

export SERVICE=uat-ir-tradecore-balcm
read -p "Press [Enter] key to create service..."
kubectl delete service $SERVICE
kubectl create -f $SERVICE-service.yaml
kubectl get services

