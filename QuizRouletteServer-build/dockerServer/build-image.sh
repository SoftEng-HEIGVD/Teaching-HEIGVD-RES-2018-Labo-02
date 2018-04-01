#!/bin/bash

# Ask maven to build the executable jar file from the source files
mvn clean install --file ..pom.xml

# Copy the executable jar file in the current directory
cp /adam/modules/res/labos/labo02/Teaching-HEIGVD-RES-2018-Labo-02/QuizRouletteServer-build/QuizRouletteServer-code/target/QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar .

# Build the Docker image locally
docker build --tag labo02-server .

