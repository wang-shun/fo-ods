export PROJECT_ID=fo-ods
export SERVICE=tradecore-ult
mvn clean install -DskipTests
docker build -t eu.gcr.io/$PROJECT_ID/$SERVICE:v1 .

gcloud docker -- push eu.gcr.io/$PROJECT_ID/$SERVICE:v1



export PUBSUB_TOPIC=ultpullpubsubtopic
#docker run --rm -p 8080:8080 eu.gcr.io/${PROJECT_ID}/$SERVICE:v1 -e "PUBSUB_TOPIC=ultpullpubsubtopic"
