FROM openjdk:21
EXPOSE 8080
ADD target/devRate-*.jar devRate.jar
ENTRYPOINT ["java", "-jar", "/devRate.jar"]