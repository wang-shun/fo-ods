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
kubectl delete deployment tradecore-servicelayer
read -p "Press [Enter] key to start deployment..."
kubectl create -f lifeLoadercreatordeployMaster.yaml
kubectl get deployment
read -p "Press [Enter] key to continue to create service..."


kubectl create -f lifeLoadercreatorservice.yaml
kubectl get services


#kubectl expose deployment tradecore-ult-pod  --type=LoadBalancer