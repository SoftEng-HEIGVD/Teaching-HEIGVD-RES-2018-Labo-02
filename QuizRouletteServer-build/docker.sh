echo "copy of jar file in docker-server/src"
cp QuizRouletteServer-code/target/QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar Docker/src/
echo "build of Dockerfile"
docker build -t labo2-server-java ./Docker
echo "run image"
docker run -p 8080:1313 labo2-server-java
