set -e
mvn clean package
docker-compose build payment-service
