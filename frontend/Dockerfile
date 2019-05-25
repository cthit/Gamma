FROM node:10.15 as build

COPY /package.json /
RUN ping -c 6 8.8.8.8
RUN yarn install --production
RUN yarn global add react-scripts 

COPY . .
RUN yarn build

from nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /build /usr/share/nginx/html
