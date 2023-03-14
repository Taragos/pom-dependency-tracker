# POM Dependency Tracker

A REST-API based spring project for tracking and searching maven project dependencies.  
This project uses a Neo4j-Database to efficiently store and search maven information.  
The information can be imported via the REST-API, e.g. as part of an CI-pipeline.

## Installation

Currently, the project only officially supports docker as a deployment method.  
Since it's a java / spring based project, a typical java deployment should also work.

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

## Config for local tests

```properties
spring.neo4j.uri=bolt://localhost:7687
spring.neo4j.authentication.username=neo4j
spring.neo4j.authentication.password=password
spring.auth.user.username=user
spring.auth.user.password=password
spring.auth.system.username=system
spring.auth.system.password=password
management.endpoint.health.probes.enabled=true
server.servlet.context-path=/elvis.pom-dependency-tracker/
```