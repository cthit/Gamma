FROM node:10.15

RUN mkdir -p /usr/src/app/
RUN chown -R node /usr/src/app

USER node

WORKDIR /usr/src/app

COPY package.json .

RUN yarn install --network-timeout 10000000
RUN yarn global add react-scripts

EXPOSE 3000

COPY /src .
COPY /public .

CMD yarn start

