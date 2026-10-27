# Spring Boot backend image.
# Build context: Server/DeVito-Styles  (see scripts/2-build-backend.sh)

# ---- Build stage -------------------------------------------------------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src

# Download dependencies first so they're cached between builds
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src

# The pom has no spring-boot-maven-plugin in <build>, so `package` alone
# makes a plain jar. Running `spring-boot:repackage` turns it into a runnable
# fat jar. Tests are skipped here; repository tests need a live database.
RUN mvn -B -q -Dmaven.test.skip=true package spring-boot:repackage \
 && cp "$(ls target/*.jar | grep -v -e '\.original$' -e '/original-' | head -n 1)" /src/app.jar

# ---- Runtime stage -----------------------------------------------------
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN groupadd --system app && useradd --system --gid app app
COPY --from=build /src/app.jar /app/app.jar
USER app

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
