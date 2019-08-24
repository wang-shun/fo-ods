FROM gcr.io/google_appengine/openjdk

ADD target/fo-ods-ulg-0.1.0-SNAPSHOT.jar /

ADD src/main/resources/cash-eq/*.json src/main/resources/cash-eq/

CMD ["java", "-jar", "fo-ods-ulg-0.1.0-SNAPSHOT.jar", "cash-eq", "tradecore", "100000", "5000"]
