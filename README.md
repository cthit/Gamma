# Delta

[![Build Status](https://travis-ci.com/cthit/Delta.svg?branch=develop)](https://travis-ci.com/cthit/Delta)

# Delta is Chalmers IT section account system #
---

## More information about Delta can be found on the [Wiki](https://github.com/cthit/Delta/wiki) ##

## Build setup ##

### For production ###
it's real easy. Just replace the environment variables with suitable value (see wiki)
and run:

`docker-compose up -f prod.docker-compose.yml --build`

Depending on your build system, things might be different, and a proxy is probably needed for a real production version of Delta. 

### Development ###
run

`docker-compose up --build` to build the frontend, database, databasemonitoring, and all microservices that's needed for delta.

then you will need to start the server, this is done by running the Java code in the backend, and is probably best done through an IDE.
