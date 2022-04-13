set -e
echo "Tests are running..."
cd integration-test-cucumber
mvn test
cd ..
echo "Done!"
