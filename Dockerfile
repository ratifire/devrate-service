FROM openjdk:21 as builder
WORKDIR /devRate
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN tr -d '\r' < mvnw > mvnw_unix && chmod +x mvnw_unix && ./mvnw_unix dependency:go-offline
COPY ./src ./src
RUN ./mvnw_unix clean install

FROM openjdk:21
WORKDIR /app
COPY --from=builder /devRate/target/*.jar /devRate/*.jar
EXPOSE 8088
ENTRYPOINT ["java", "-jar", "/devRate/*.jar"]
