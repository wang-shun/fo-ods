export PROJECT_ID=fo-ods
export SERVICE=tradecore-dlt
mvn clean install -DskipTests
#docker build -t gcr.io/$PROJECT_ID/$SERVICE:v1 .

#gcloud docker -- push gcr.io/$PROJECT_ID/$SERVICE:v1

gcloud container builds submit --tag gcr.io/$PROJECT_ID/$SERVICE:v1 .