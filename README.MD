# Monitoring a Murmuration of Microservices

This repository demonstrates the use of the following monitoring stack:

* Prometheus
* Promtail
* Loki
* Grafana

It also shows how to integrate these technologies with Spring Boot.

Note this setup is entirely unauthenticated and is not a production-ready example.

Spring Boot is customised to output metrics in a format compatible for Prometheus and JSON
logs in a format compatible for Promtail.

## Requirements

* Docker
* Java 16
* Maven

## Structure

* words - a random word generator as a Spring Boot microservice
* game - a simple hangman game as a Spring Boot microservice
* infrastructure - spins up both microservices and the monitoring stack
* random-client - an automated client that simulates players playing the game

## Building

Both microservices can be built by running the following in the root directory:

    mvn package

This will create a local docker image for each microservice.

All other docker images will be built automatically when running docker compose.

## Running

Run the following inside the `infrastructure` directory:

    docker-compose up -d

To destroy the cluster, run:

    docker-compose down

Note that no data is persisted so bringing it down will wipe all transient data e.g. collected metrics or any changes
made to Grafana dashboards.

## Using

* [Prometheus](http://localhost:9090)
* [Grafana](http://localhost:3000)
* [Game Metrics](http://localhost:8080/metrics)
* [Words Metrics](http://localhost:8081/metrics)

See `requests.http` for how to interact with the microservices.

The Grafana dashboard tailored for the game metrics is called "Game" and should be available
from the Grafana home page.

You may also wish to import the following JVM dashboard to get more generic insights into how
the microservices are performing: [https://grafana.com/grafana/dashboards/4701].

## Simulate players

To better see the metrics in Grafana, you can run the random-client to simulate players playing
the game.

An executable jar will have been built for you if you ran the build instructions above. This
can be executed as follows:

    java -jar random-client/target/random-client-1.0-SNAPSHOT.jar

This will run continuously until you kill the process. Watch the Grafana game dashboard to see
how well the players are doing.