FROM node:13.2.0 as build
WORKDIR /usr/src/app
COPY package.json yarn.lock ./
RUN yarn --production --frozen-lockfile --network-timeout 10000000
COPY src src
COPY public public
RUN yarn build
FROM nginx:1.16.0-alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /usr/src/app/build /usr/share/nginx/html
RUN nginx -t
