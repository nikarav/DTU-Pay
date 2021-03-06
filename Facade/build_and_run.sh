#!/bin/bash
set -e

pushd ../DTUPay-AccountManagement/account-management-service
./build.sh
popd

pushd ../DTUPay-Payment/payment-service
./build.sh
popd

pushd ../DTUPay-TokenManagement/token-management
./build.sh
popd

pushd ../Reporting/reporting-service
./build.sh
popd

pushd facade-service
####
docker image prune -f
docker-compose up -d rabbitMq

####
sleep 5

mvn package
# Create a new docker image if necessary.
# Restarts the container with the new image if necessary
# The server stays running.
# To terminate the server run docker-compose down in the
# code-with-quarkus direcgtory
docker-compose build DTUPay
docker-compose up -d DTUPay

docker-compose up -d token-service
docker-compose up -d account-service
docker-compose up -d payment-service
docker-compose up -d reporting-service

# clean up images
docker image prune -f 
popd

# Give the Web server a chance to finish start up
sleep 2 

#pushd facade-cucumber
#mvn test
#popd

