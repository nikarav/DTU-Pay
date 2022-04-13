FROM adoptopenjdk:11-jre-hotspot
WORKDIR /usr/src
COPY target/lib /usr/src/lib
COPY target/reporting-service-1.0.0-SNAPSHOT.jar /usr/src
CMD java -Xmx64m \
-jar reporting-service-1.0.0-SNAPSHOT.jar