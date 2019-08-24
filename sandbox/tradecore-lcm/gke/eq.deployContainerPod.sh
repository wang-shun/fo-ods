#Creating the pod
#kubectl create -f lifeLoadercreatorCreate.yaml

#kubectl get pods
#read -p "Press [Enter] key to start deployment..."
#fx.tradecore-lcm.deploy
#eq.tradecore-lcm.deploy
#ir.tradecore-lcm.deploy

#fxtradecore-lcm
#eqtradecore-lcm
#irtradecore-lcm
export ASSET=sit-eq-tradecore

gcloud container clusters get-credentials $ASSET-lcm --zone europe-west1-c --project fo-ods
kubectl delete deployment $ASSET-lcm
read -p "Press [Enter] key to start deployment..."
kubectl create -f $ASSET-lcm-deploy.yaml
kubectl get deployments
read -p "Press [Enter] key to continue to create service..."
kubectl create -f $ASSET-lcm-service.yaml
kubectl get services


#kubectl expose deployment tradecore-ult-pod  --type=LoadBalancer