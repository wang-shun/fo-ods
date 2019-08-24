export PROJECT_ID=fo-ods
export SERVICE=dltcloudstorage
docker build -t eu.gcr.io/$PROJECT_ID/$SERVICE:v1 .

gcloud docker -- push eu.gcr.io/$PROJECT_ID/$SERVICE:v1




#docker run --rm -p 8080:8080 eu.gcr.io/${PROJECT_ID}/$SERVICE:v1 -e "deep=purple"