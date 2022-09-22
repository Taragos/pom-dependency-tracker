# POM Dependency Tracker

A REST-API based spring project for tracking and searching maven project dependencies.  
This project uses a Neo4j-Database to efficiently store and search maven information.  
The information can be imported via the REST-API, e.g. as part of an CI-pipeline.

## Installation

Currently the project only officially supports docker as a deployment method.  
Since it's a java / spring based project, normal a normal java deployment should also work.

**Docker-Compose (recommended)**

```
curl -O github.com/Taragos/pom-dependency-tracker/infrastructure/docker-compose.yaml
docker-compose up -d
```

**Pure Docker** (requires a neo4j db)

```
docker run -p 8080:8080 \
    -e NEO4J_URI=bolt://localhost:7687 \
    -e NEO4J_AUTH_USERNAME=neo4j \
    -e NEO4J_AUTH_PASSWORD=test \
    -e USER_AUTH_USERNAME=user \
    -e USER_AUTH_PASSWORD=userPassword \
    -e SYSTEM_AUTH_USERNAME=system \
    -e SYSTEM_AUTH_PASSWORD=systemPassword \
    docker.io/taragos/pom-dependency-tracker:latest
```

## Todos

* [x] Version and Release Control
* [x] You set up the frameworks and tools that you described in the conception phase.
* [x] You select third-party libraries you can build upon.
* [x] You refine the requirements gathered in the conception phase.
* [x] You implement the modules/components outlined in your building block diagram.
* [x] You further develop your software and system documentation.
* [x] You document important design decisions including rationales.
* [ ] You comment your source code to make it easier for humans to understand.
* [ ] You prepare test data and perform adequate software tests.