FROM gradle:jdk11 as gradleimage
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN ./gradlew build -x test

CMD ["./gradlew", "bootRunDev"]