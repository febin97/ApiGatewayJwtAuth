FROM openjdk:11
WORKDIR usr/src
COPY ./target/authentication-service-0.0.1-SNAPSHOT.jar /usr/src/authentication.jar
ENTRYPOINT ["java","-jar","authentication.jar"]