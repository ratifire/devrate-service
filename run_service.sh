#!/bin/bash
echo "build service"
mvn clean install

echo "Stop current services"
docker-compose down

echo "Remove devrate-service image"
docker rmi devrate-service-app

echo "Restart service"
docker-compose up


