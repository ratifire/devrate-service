FROM openjdk:21
EXPOSE 8080
ENV JAR_FILE=target/devRate-*.jar
COPY ${JAR_FILE} /devRate.jar
COPY newrelic-dev/newrelic-dev.jar /newrelic-dev.jar
COPY newrelic-dev/newrelic-dev.yml /newrelic-dev.yml
ENTRYPOINT ["java", "-javaagent:/newrelic-dev.jar", "-jar", "/devRate.jar"]
