FROM openjdk:17-alpine

WORKDIR /

COPY build/libs/user-service-1.0-SNAPSHOT.jar /opt/app/user-service-1.0-SNAPSHOT.jar

CMD java -Xmx1024m -jar /opt/app/user-service-1.0-SNAPSHOT.jar