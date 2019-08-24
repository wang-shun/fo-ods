kubectl create -f lifeLoaderdummyservice.yaml
kubectl get services

#Creating the pod
#kubectl create -f lifeLoaderdummyCreate.yaml

#kubectl get pods
#read -p "Press [Enter] key to start deployment..."
kubectl delete deployment tradecore-ult
kubectl create -f lifeloaderdummydeploy.yaml
kubectl get deployment
read -p "Press [Enter] key to continue to create service..."


#kubectl expose deployment tradecore-ult-pod  --type=LoadBalancer