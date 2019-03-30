FROM node:latest

RUN mkdir /usr/src/app
WORKDIR /usr/src/app

COPY package.json .

ENV BACKEND_URL http://localhost:9090

ENV REDIRECT_URI http://localhost:8080

ENV NODE_ENV production

RUN npm install

RUN npm ci --only=production

COPY . . 

EXPOSE 8082

CMD ["npm", "start"]