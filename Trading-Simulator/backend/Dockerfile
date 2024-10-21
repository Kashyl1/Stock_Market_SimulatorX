FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

COPY target/backend-0.0.1-SNAPSHOT.jar Stock_Market_Simulator.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Stock_Market_Simulator.jar"]
