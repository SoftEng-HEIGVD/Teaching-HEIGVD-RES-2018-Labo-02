FROM java:openjdk-8-jre-alpine 
ADD src /opt/src/QuizRouletteServer-build/QuizRouletteServer-code/target
WORKDIR /opt/src/QuizRouletteServer-build/QuizRouletteServer-code/target
ENTRYPOINT ["java","-jar","QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar"]
