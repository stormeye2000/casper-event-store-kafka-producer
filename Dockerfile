FROM gradle:jdk11 as gradleimage
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build

FROM openjdk:11-jre-slim
COPY --from=gradleimage /home/gradle/source/build/libs/*.jar /app/app.jar
WORKDIR /app

CMD ["java", "-XX:+UseContainerSupport", "-jar", "app.jar", "--server.port=8080 --spring.profiles.active=dev"]