FROM gcr.io/google_appengine/openjdk

ADD target/tradecore-bridge-1.0.0-RELEASE.jar apps/

ADD target/dep-lib/*.jar apps/dep-lib/

CMD ["java", "-jar", "apps/tradecore-bridge-1.0.0-RELEASE.jar"]
