FROM gcr.io/google_appengine/openjdk

ADD target/tradecore-store-0.1.0-SNAPSHOT.jar apps/

ADD target/dep-lib/*.jar apps/dep-lib/

ADD src/main/resources/ src/main/resources/

CMD ["java", "-jar", "apps/tradecore-store-0.1.0-SNAPSHOT.jar", "5555"]
