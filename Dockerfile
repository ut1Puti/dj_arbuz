FROM gradle:7.5.1-jdk17-alpine

WORKDIR /usr/dj_arbuz

COPY ./ /usr/dj_arbuz

EXPOSE 8080

ENTRYPOINT ["bash", "./telegram/src/main/resources/scripts/run/run.sh"]
