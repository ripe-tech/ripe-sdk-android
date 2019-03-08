FROM hivesolutions/python:latest

LABEL version="1.0"
LABEL maintainer="Platforme <development@platforme.com>"

EXPOSE 8080

ENV LEVEL INFO
ENV SERVER netius
ENV SERVER_ENCODING gzip
ENV HOST 0.0.0.0
ENV PORT 8080

ADD ripe /ripe
ADD build.gradle /
ADD gradlew /

RUN apk update && apk add openjdk8-jre

CMD ["/usr/bin/java", "-version"]
RUN ./gradlew dokka
