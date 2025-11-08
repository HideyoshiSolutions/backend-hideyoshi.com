FROM maven:3.9.3-ibm-semeru-17-focal AS build

WORKDIR /home/app

COPY pom.xml mvnw ./
COPY .mvn/ .mvn/

# Download dependencies into /root/.m2 (use BuildKit cache if available).
# If BuildKit isn't enabled this still works as a normal mvn dependency:go-offline.
RUN --mount=type=cache,target=/root/.m2 mvn -B -Dmaven.test.skip=true dependency:go-offline

COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn -B -Dmaven.test.skip=true package

#
# Package stage
#
FROM ibm-semeru-runtimes:open-17-jdk-focal
WORKDIR /app

# Copy final artifact
COPY --from=build /home/app/target/*.jar ./app.jar

ENTRYPOINT ["java","-XX:TieredStopAtLevel=1","-Xverify:none","-jar","/app/app.jar"]
