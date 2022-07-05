FROM openjdk:11-jdk-slim
ENV PORT 8080
EXPOSE 8080
COPY build/libs/*.jar /opt/app.jar
WORKDIR /opt
CMD ["java", "-XX:+UseContainerSupport", "-jar", "app.jar", "--server.port=8080"]