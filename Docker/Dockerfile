FROM java:8
Maintainer Yosra Harbaoui <yosra.harbaoui@heig-vd.ch>

COPY QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar /opt/app/server.jar

EXPOSE 1313

ENTRYPOINT ["java", "-jar", "/opt/app/server.jar"]