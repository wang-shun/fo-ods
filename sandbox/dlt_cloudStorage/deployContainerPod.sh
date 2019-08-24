#Creating the pod
#kubectl create -f dltdummyCreate.yaml

#kubectl get pods
#read -p "Press [Enter] key to start deployment..."

kubectl create -f dltdummydeploy.yaml
kubectl get deployment
read -p "Press [Enter] key to continue to create service..."


#kubectl create -f dltdummyservice.yaml
#kubectl get services


#kubectl expose deployment tradecore-ult-pod  --type=LoadBalancer