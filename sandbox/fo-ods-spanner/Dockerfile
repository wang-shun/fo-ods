FROM gcr.io/google-appengine/openjdk
COPY target/applib /applib
COPY trades.txt /
COPY target/spanner-1.0-SNAPSHOT.jar $APP_DESTINATION
