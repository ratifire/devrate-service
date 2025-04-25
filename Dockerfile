FROM openjdk:21
EXPOSE 8080
ENV JAR_FILE=target/devRate-*.jar
COPY ${JAR_FILE} /devRate.jar
COPY newrelic/newrelic.jar /newrelic.jar
COPY newrelic/newrelic.yml /newrelic.yml
ENTRYPOINT ["java", "-javaagent:/newrelic.jar", "-jar", "/devRate.jar"]
