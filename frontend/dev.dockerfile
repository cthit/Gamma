FROM node:14 as build
WORKDIR /usr/src/app
COPY package.json yarn.lock jsconfig.json .eslintrc ./
RUN yarn install --network-timeout 10000000
COPY .env.development .env
COPY src src
COPY public public
CMD yarn start

