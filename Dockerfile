FROM maven:latest as builder
WORKDIR /devRate
COPY mvnw pom.xml ./
COPY . .
RUN mvn clean install

FROM maven:latest
WORKDIR /devRate
COPY --from=builder /devRate/target/*.jar /devRate/*.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/devRate/*.jar"]







