FROM node:13.2.0 as build

COPY /package.json /
RUN yarn install --production
RUN yarn global add react-scripts

COPY /src /src
COPY /public /public
COPY nginx.conf .
RUN yarn build

from nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /build /usr/share/nginx/html