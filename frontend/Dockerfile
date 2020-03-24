FROM node:13.2.0 as build

WORKDIR /usr/src/app

COPY package.json .
COPY yarn.lock .

RUN chown -R node /usr/src/app

USER node

RUN yarn install --production --frozen-lockfile --network-timeout 10000000
RUN yarn global add react-scripts

COPY src src
COPY public public
COPY nginx.conf .

RUN yarn build

FROM nginx:1.16.0-alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /usr/src/app/build /usr/share/nginx/html
CMD ["nginx", "-g", "daemon off;"]