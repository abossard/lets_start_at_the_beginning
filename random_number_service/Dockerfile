FROM node:10
RUN mkdir /app
WORKDIR /app
COPY package.json .
COPY package-lock.json .
RUN npm i
COPY server.ts .
EXPOSE 8080
CMD ["node", "-r", "ts-node/register", "/app/server.ts"]