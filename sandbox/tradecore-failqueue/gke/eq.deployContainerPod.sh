
export ASSET=eqtradecore

gcloud container clusters get-credentials $ASSET-service --zone europe-west1-c --project fo-ods
kubectl delete pod $ASSET-failqueue
read -p "Press [Enter] key to start deployment..."
#Creating the pod
kubectl create -f $ASSET-failqueue.deploy.yaml

kubectl get pods
