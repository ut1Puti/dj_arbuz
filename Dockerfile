FROM maven:3.8.6-amazoncorretto-18

RUN mkdir -p /usr/dj_arbuz
WORKDIR /usr/dj_arbuz

COPY . /usr/dj_arbuz

EXPOSE 8080

ENTRYPOINT ["mvn", "compile"]
CMD ["mvn", "exec:java"]
