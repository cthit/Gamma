from node:9.11.1 as build

COPY /package.json /

RUN yarn install --production
RUN yarn global add react-scripts 

COPY . .
RUN yarn build

from nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY --from=build /build /usr/share/nginx/html
