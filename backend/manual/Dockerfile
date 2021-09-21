#FROM adoptopenjdk:16-jre-hotspot
#ARG JAR_FILE=./target/manual-0.0.1-SNAPSHOT.jar
#COPY ${JAR_FILE} airdocs.jar
#EXPOSE 8080
#ENTRYPOINT ["java", "-jar", "airdocs.jar"]

FROM adoptopenjdk/openjdk16:jdk-16.0.1_9-alpine as build
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw clean package -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM adoptopenjdk/openjdk16:jdk-16.0.1_9-alpine
ARG DEPENDENCY=/app/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.slim.manual.ManualApplication"]