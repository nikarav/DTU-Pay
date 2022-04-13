FROM adoptopenjdk:11-jre-hotspot
WORKDIR /usr/src
COPY target/lib /usr/src/lib
COPY target/payment-service-1.0.0-SNAPSHOT.jar /usr/src/
CMD java -Xmx64m \
-jar payment-service-1.0.0-SNAPSHOT.jar