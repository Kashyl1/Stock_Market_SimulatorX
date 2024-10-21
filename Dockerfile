FROM node:16 AS build-frontend
WORKDIR /Trading-Simulator/frontend
COPY Trading-Simulator/frontend/package.json Trading-Simulator/frontend/package-lock.json ./
RUN npm install
COPY Trading-Simulator/frontend ./
RUN npm run build

FROM maven:3.8.4-openjdk-17 AS build-backend
WORKDIR /Trading-Simulator/backend

COPY Trading-Simulator/backend/pom.xml ./
COPY Trading-Simulator/backend/src ./src

COPY --from=build-frontend /Trading-Simulator/frontend/build/ ./src/main/resources/static/

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY --from=build-backend /Trading-Simulator/backend/target/backend-0.0.1-SNAPSHOT.jar Stock_Market_Simulator.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "Stock_Market_Simulator.jar"]
