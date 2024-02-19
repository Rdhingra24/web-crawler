FROM bellsoft/liberica-openjdk-alpine:17

COPY target/crawler-jar-with-dependencies.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
