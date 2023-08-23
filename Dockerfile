#
# Build stage
#
FROM maven:3.9.3-ibm-semeru-17-focal AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn -Dmaven.test.skip -f /home/app/pom.xml clean package

#
# Package stage
#
FROM ibm-semeru-runtimes:open-11.0.19_7-jre-focal

COPY --from=build /home/app/target/*.jar app.jar
COPY src/main/resources/* credentials/
ENTRYPOINT ["java","-jar","/app.jar"]