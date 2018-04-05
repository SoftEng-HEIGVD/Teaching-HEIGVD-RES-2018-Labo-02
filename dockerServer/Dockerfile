#Docker server Java
FROM java:8
ADD /src/QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar /opt/src/
WORKDIR /opt/src/
ENTRYPOINT ["java","-jar","QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar"]