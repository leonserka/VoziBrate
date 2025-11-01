# Koristimo službenu OpenJDK sliku (verzija 25 jer ti je JDK 25)
FROM openjdk:25-jdk

# Postavi radni direktorij unutar kontejnera
WORKDIR /app

# Kopiraj izgrađeni JAR u kontejner
COPY target/bus-tracker-0.0.1-SNAPSHOT.jar app.jar

# Pokreni aplikaciju
ENTRYPOINT ["java","-jar","app.jar"]
