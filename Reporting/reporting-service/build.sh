set -e
mvn clean package
docker-compose build reporting-service
