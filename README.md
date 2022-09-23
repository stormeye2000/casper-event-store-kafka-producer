## Casper Event Store Kafka Producer



Prototype Java 11 kafka producer

It monitors the three casper emitters:

- `/events/deploys` for DeployAccepted events
- `/events/sigs` for FinalitySignature events
- `/events/main` for all other event types

Currently connecting to the test endpoint:

http://65.21.235.219:9999

Events will be left in the kafka topics queue until a Consumer picks them up

### To Run Locally

Install Kafka and Zookeeper and run from kafka bin folder:

```bash
zookeeper-server-start ../../libexec/config/zookeeper.properties

kakfka-server-start ../../libexec/config/server.properties

#Create the topics, only needed on the initial run

kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic main

kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic deploys

kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic sigs
```

