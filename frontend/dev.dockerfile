FROM node:9.11.1

RUN mkdir -p /usr/src/app/node_modules
RUN chown -R node /usr/src/app
USER root

WORKDIR /usr/src/app

RUN chmod -R 777 /usr/src/app/node_modules

RUN yarn install --verbose
RUN yarn global add react-scripts --verbose

CMD yarn start --verbose

EXPOSE 3000
