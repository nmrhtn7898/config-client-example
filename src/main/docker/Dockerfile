FROM openjdk:8-jdk-alpine
RUN apk update && apk upgrade && apk add netcat-openbsd && apk add curl
ARG finalName
COPY ${finalName} app.jar
COPY run.sh run.sh
RUN chmod +x run.sh
ENTRYPOINT ["./run.sh"]