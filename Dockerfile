FROM openjdk:21
EXPOSE 8080
ARG ENVIRONMENT=dev
ENV JAR_FILE=target/devRate-*.jar
COPY ${JAR_FILE} /devRate.jar
COPY newrelic-${ENVIRONMENT}/newrelic-${ENVIRONMENT}.jar /newrelic-${ENVIRONMENT}.jar
COPY newrelic-${ENVIRONMENT}/newrelic-${ENVIRONMENT}.yml /newrelic-${ENVIRONMENT}.yml
ENTRYPOINT ["java", "-javaagent:/newrelic-${ENVIRONMENT}.jar", "-Dnewrelic.config.file=/newrelic-${ENVIRONMENT}.yml", "-jar", "/devRate.jar"]
