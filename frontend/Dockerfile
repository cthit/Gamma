FROM node:10.15 as build

COPY /package.json /
RUN yarn install --network-timeout 1000000 --production
RUN yarn global add react-scripts 

COPY . .
RUN yarn build

from nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /build /usr/share/nginx/html
