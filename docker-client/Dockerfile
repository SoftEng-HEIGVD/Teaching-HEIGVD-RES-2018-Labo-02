
FROM node:alpine

WORKDIR /opt/app

ADD client.js /opt/app/client.js
ADD RES.csv /opt/data/RES.csv

ENTRYPOINT ["node", "client.js"]
