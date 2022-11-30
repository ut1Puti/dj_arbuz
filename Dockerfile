FROM openjdk:17-alpine

WORKDIR /usr/dj_arbuz

COPY ./ /usr/dj_arbuz

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "./telegram/build/libs/telegram-4.0.0-SNAPSHOT.jar"]
