# Use an official OpenJDK runtime as a parent image
FROM openjdk:8-jre-alpine

# set shell to bash
# source: https://stackoverflow.com/a/40944512/3128926
RUN apk update && apk add bash curl jq

# Set the working directory to /app
WORKDIR /app

# set environment
ENV github_api_link="https://api.github.com/repos/WirVsVirus-SoliD/SoliD-backend/releases/latest"

# Copy the fat jar into the container at /app
RUN wget -O app.jar $(curl --silent ${github_api_link} | jq -r '.assets[0].browser_download_url')

# Run jar file when the container launches
CMD ["java", "-jar", "app.jar"]