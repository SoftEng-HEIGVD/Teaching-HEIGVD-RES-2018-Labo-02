mkdir docker-server/src
echo "copy of jar file in docker-server/src/"
cp -f QuizRouletteServer-build/QuizRouletteServer-code/target/QuizRouletteServer-code-1.0-SNAPSHOT-standalone.jar docker-server/src/

mkdir docker-client-node
echo "copy of client.js (client) in docker-client-node/src/"
cp -f QuizRouletteClient/client.js docker-client-node/src/

mkdir docker-client-node/data
echo "copy of data"
cp -f data/RES.csv docker-client-node/data/

echo "build of Dockerfile server"
docker build -t labo2-server-java ./docker-server

echo "build of Dockerfile client"
docker build -t labo2-client-node ./docker-client-node

echo "run image server"
docker run -d -p 1313:1313 labo2-server-java

echo "run image client"
docker run -p 8080:8080 labo2-client-node


