#Docker server Java
FROM java:openjdk-8-alpine

ADD src /opt/src/

WORKDIR /opt/src/

ENTRYPOINT ["java","-jar","QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar"]
