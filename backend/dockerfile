FROM gradle:4.10-jdk10
#Gradle docker has gradle user as default
USER root


WORKDIR /app

#db is used to separate development and production databases

# Environment variables needed for Gamma to run in Production env, These must be changed!!.
COPY . /app

ENV LOGGING_FILE log/production.log

RUN mkdir -p /app
RUN chown -R gradle /app

USER gradle

RUN gradle :build -x test
# This probably should not have a static path, but instead build in a custom path.
CMD sleep 5 && java -jar -Dspring.profiles.active=production build/libs/gamma-0.0.1-SNAPSHOT.jar

