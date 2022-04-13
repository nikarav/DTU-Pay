#!/bin/bash
set -e

# Build and install the libraries
# abstracting away from using the
# RabbitMq message queue
cd message-queue-service
./build.sh
cd ..

# Build the services
#pushd student-id-service
#./build.sh
#popd
#
#pushd student-registration-service
#./build.sh
#popd
