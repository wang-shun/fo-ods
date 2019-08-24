kubectl delete pod dltstatus
kubectl create -f dlt-deployment.yaml
kubectl get deployment


#kubectl create -f dltdummyservice.yaml
#kubectl get services


#kubectl expose deployment tradecore-ult-pod  --type=LoadBalancer