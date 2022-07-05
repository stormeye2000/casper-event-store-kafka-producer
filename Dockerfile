FROM openjdk:11-jdk-slim
ENV PORT 8080
EXPOSE 8080

COPY build/libs/*.jar /app/app.jar
WORKDIR /app

RUN java -jar -Dspring.profiles.active=dev /app/app.jar

CMD ["java", "-XX:+UseContainerSupport", "-jar", "app.jar", "--server.port=8080"]