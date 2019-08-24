kubectl delete -f gke/eq-ult.pod.yaml
sleep 5
kubectl create -f gke/eq-ult.pod.yaml
