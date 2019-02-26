FROM node:latest

RUN mkdir /usr/src/app
WORKDIR /usr/src/app

COPY package.json .

RUN npm ci --only=production

COPY . . 

EXPOSE 8082

CMD ["npm", "start"]