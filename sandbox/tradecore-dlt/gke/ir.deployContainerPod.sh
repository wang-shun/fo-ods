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
export ASSET=irtradecore

gcloud container clusters get-credentials $ASSET-service --zone europe-west1-c --project fo-ods
kubectl delete deployment $ASSET-dlt
read -p "Press [Enter] key to start deployment..."
kubectl create -f $ASSET-dlt.deploy.yaml
kubectl get deployment



#kubectl expose deployment tradecore-ult-pod  --type=LoadBalancer