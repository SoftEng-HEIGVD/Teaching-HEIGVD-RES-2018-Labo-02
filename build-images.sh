#!/bin/bash

# Ask maven to build the executable jar file from the source files
mvn -f QuizRouletteServer-build/ clean install

echo "copy of jar file in docker-server"
cp QuizRouletteServer-build/QuizRouletteServer-code/target/QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar docker/src/

echo "build of Dockerfile"
docker build -t labo2-server-java ./docker

echo "run image"
docker run -p 8080:1313 labo2-server-java

read -p "Press [Enter] key to start backup..."