set -e
mvn clean package
docker-compose build account-service
