#Docker server Java
FROM node:alpine

ADD src /opt/src
ADD data /opt/data

WORKDIR /opt/src

ENTRYPOINT ["node","client.js"]
