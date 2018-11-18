FROM node:8.12-jessie

RUN mkdir -p /usr/src/app
RUN chown node /usr/src/app
USER node

WORKDIR /usr/src/app

RUN yarn
RUN yarn global add react-scripts

EXPOSE 3000

CMD yarn start