FROM maven:3-openjdk-11 AS builder

ARG repo_url="https://github.com/WirVsVirus-SoliD/SoliD-backend.git"
ARG rootdir=/app
ARG branch=master

RUN \
    git clone ${repo_url} ${rootdir} && \
    cd ${rootdir} && \
    for remote in `git branch -r`; do git branch --track ${remote#origin/} $remote; done ; \
    git checkout ${branch} && \
    mvn package

FROM openjdk:11-jre-slim-buster

ARG jar_file=/app/target/solid-backend.jar

WORKDIR /app

COPY --from=builder ${jar_file} ./app.jar

CMD ["java", "-jar", "app.jar"]
