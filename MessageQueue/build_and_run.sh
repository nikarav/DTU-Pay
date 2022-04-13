#!/bin/bash
set -e

pushd message-queue-service
./build.sh
docker image prune -f
docker-compose up -d rabbitMq
docker-compose down
# clean up images
docker image prune -f
popd


