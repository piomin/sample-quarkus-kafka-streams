# Kafka Streams with Quarkus [![Twitter](https://img.shields.io/twitter/follow/piotr_minkowski.svg?style=social&logo=twitter&label=Follow%20Me)](https://twitter.com/piotr_minkowski)

[![CircleCI](https://circleci.com/gh/piomin/sample-quarkus-kafka-streams.svg?style=svg)](https://circleci.com/gh/piomin/sample-quarkus-kafka-streams)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-black.svg)](https://sonarcloud.io/dashboard?id=piomin_sample-quarkus-kafka-streams)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-quarkus-kafka-streams&metric=bugs)](https://sonarcloud.io/dashboard?id=piomin_sample-quarkus-kafka-streams)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-quarkus-kafka-streams&metric=coverage)](https://sonarcloud.io/dashboard?id=piomin_sample-quarkus-kafka-streams)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=piomin_sample-quarkus-kafka-streams&metric=ncloc)](https://sonarcloud.io/dashboard?id=piomin_sample-quarkus-kafka-streams)

This repository is related with the article: [Kafka Streams with Quarkus](https://piotrminkowski.com/2021/11/24/kafka-streams-with-quarkus/).

It shows how to build a simple stock market application that consumes Kafka Streams.

The following picture illustrates our architecture:

<img src="https://i1.wp.com/piotrminkowski.com/wp-content/uploads/2021/11/Screenshot-2021-11-23-at-09.52.58.png?ssl=1" title="Architecture"><br/>

## Run locally

### Usage
You need to have Maven, JDK11+ and Docker (or Testcontainers Cloud Agent) installed on your local machine.

First, run the `order-service`:
```shell
cd order-service
mvn quarkus:dev
```

It will automatically start Kafka using Redpanda (Kafka API compatible platform). \
Then, run the `stock-service`:
```shell
cd stock-service
mvn quarkus:dev
```

Observe the logs. \
You can access Quarkus Dev UI console: `http://localhost:8080/q/dev`. \
You can also call some REST endpoints with analytical data:
```shell
$ curl http://localhost:8080/transactions/products
$ curl http://localhost:8080/transactions/products/3
$ curl http://localhost:8080/transactions/products/5
```
