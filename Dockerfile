FROM openjdk:14-jdk-alpine
LABEL maintainer=CoderNoOne
EXPOSE 8080
COPY target/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java --enable-preview -jar app.jar"]
