# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jre-slim-buster

ARG branch=master

# Set the working directory to /app
WORKDIR /app

# set environment
ENV github_api_link="https://api.github.com/repos/WirVsVirus-SoliD/SoliD-backend/releases/tags/${branch}"

# set shell to bash
# source: https://stackoverflow.com/a/40944512/3128926
RUN apt update && apt install -y bash curl jq

# Copy the fat jar into the container at /app
RUN curl -L -o app.jar $(curl --silent ${github_api_link} | jq -r '.assets[0].browser_download_url')

# Run jar file when the container launches
CMD ["java", "-jar", "app.jar"]
