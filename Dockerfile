FROM maven:3.8.6-amazoncorretto-18

RUN mkdir -p /usr/dj_arbuz
WORKDIR /usr/dj_arbuz

COPY ./src/main/resources/scripts/postgres /usr/dj_arbuz/src/main/resources/scripts/postgres
COPY ./src/main/resources/scripts/run /usr/dj_arbuz/src/main/resources/scripts/run
COPY ./src/main/java /usr/dj_arbuz/src/main/java
COPY ./src/main/resources/websrc /usr/dj_arbuz/src/main/resources/websrc
COPY ./src/test/java /usr/dj_arbuz/src/test/java
COPY ./src/test/resources/vk_tests /usr/dj_arbuz/src/test/resources/vk_tests
COPY ./src/test/resources/websrc /usr/dj_arbuz/src/test/resources/websrc
COPY ./src/test/resources/anonsrc /usr/dj_arbuz/src/test/resources/anonsrc
COPY ./Dockerfile /usr/dj_arbuz
COPY ./docker-compose.yaml /usr/dj_arbuz
COPY ./pom.xml /usr/dj_arbuz

EXPOSE 8080

ENTRYPOINT ["bash", "./src/main/resources/scripts/run/run.sh"]
