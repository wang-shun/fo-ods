FROM gcr.io/google_appengine/openjdk

ADD target/tradecore-vs-0.1.0-SNAPSHOT.jar apps/

ADD target/dep-lib/*.jar apps/dep-lib/

ADD src/main/resources/ src/main/resources/

CMD ["java", "-jar", "apps/tradecore-vs-0.1.0-SNAPSHOT.jar"]
