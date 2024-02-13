FROM openjdk:21
EXPOSE 8080
ENV JAR_FILE=target/devRate-*.jar
COPY ${JAR_FILE} /devRate.jar
ENTRYPOINT ["java", "-jar", "/devRate.jar"]
