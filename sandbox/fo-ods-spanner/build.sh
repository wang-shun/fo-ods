echo ------ building the project ----------
mvn clean install

echo ------ building the docker image ----------
docker build -t gcr.io/fo-ods/spanner:v3 .

echo ------ uploading the docker image ----------
gcloud docker -- push gcr.io/fo-ods/spanner:v3

echo ------ done ----------