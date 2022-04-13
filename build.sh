set -e

cd MessageQueue/message-queue-service
./build.sh
cd ../..

cd Reporting/reporting-service
./build.sh
cd ../..

cd DTUPay-TokenManagement/token-management
./build.sh
cd ../..

cd DTUPay-Payment/payment-service
./build.sh
cd ../..

cd DTUPay-AccountManagement/account-management-service
./build.sh
cd ../..

cd Facade/facade-service
./build.sh
cd ../..

cd IntegrationTest
./build_and_run.sh
cd ..

