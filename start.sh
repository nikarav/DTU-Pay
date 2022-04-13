set -e
docker image prune -f
docker-compose up -d account-service facade-service payment-service reporting-service token-service

read -p "Press enter to shut down service"

docker-compose down
