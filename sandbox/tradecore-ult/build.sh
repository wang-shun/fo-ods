export PROJECT_ID=fo-ods
export SERVICE=tradecore-ult
mvn clean install -DskipTests
docker build -t gcr.io/$PROJECT_ID/$SERVICE:v1 .

gcloud docker -- push gcr.io/$PROJECT_ID/$SERVICE:v1