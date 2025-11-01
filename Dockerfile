FROM openjdk:25-jdk

WORKDIR /app

COPY target/bus-tracker-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
