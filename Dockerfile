FROM maven:3.8.6-amazoncorretto-18

RUN mkdir -p /usr/src/
WORKDIR /usr/src/

COPY . /usr/src/
ADD scripts/script.sh /docker-entrypoint-initdb.d/script.sh

EXPOSE 8080

CMD ["mvn", "exec:java"]
