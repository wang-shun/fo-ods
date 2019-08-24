#!/usr/bin/env bash

#!/usr/bin/env bash

export PROJECT_ID=fo-ods
export ZONE=europe-west1-c
export CLUSTER_VERSION=1.7.5


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