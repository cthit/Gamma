FROM openjdk:10.0-jdk

RUN useradd user

RUN mkdir -p /app
RUN chown user /app
WORKDIR /app


USER user

CMD ./gradlew --project-cache-dir /tmp/gradle-cache bootRun