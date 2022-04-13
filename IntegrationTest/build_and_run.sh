#!/bin/bash
set -e

./deploy.sh
sleep 5
./test.sh

docker-compose down
