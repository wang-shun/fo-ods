FROM gcr.io/google-appengine/openjdk
COPY target/applib /applib
COPY target/tradecore-lcm-0.0.1-SNAPSHOT.jar $APP_DESTINATION
EXPOSE 8080/tcp
EXPOSE 3000/tcp