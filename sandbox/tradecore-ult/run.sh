kubectl delete -f gke/eq-ult.job.yaml
sleep 5
kubectl create -f gke/eq-ult.job.yaml
