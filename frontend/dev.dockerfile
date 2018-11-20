FROM node:11.2

RUN mkdir -p /usr/src/app/
RUN chown -R node /usr/src/app

USER node


WORKDIR /usr/src/app

COPY package.json .

RUN yarn install
RUN yarn global add react-scripts

EXPOSE 3000


CMD yarn start

