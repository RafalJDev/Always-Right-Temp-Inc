# Always-Right-Temp-Inc

# Anomaly Detector

Company: Always Right Temp Inc

Dev team:
Engineering Manager: Rafał Jaszczyński

Team lead: Rafał Jaszczyński

Senior dev: Rafał Jaszczyński

Senior dev: Rafał Jaszczyński

Senior dev: Rafał Jaszczyński

Senior QA dev: Rafał Jaszczyński

Product Owner: Rafał Jaszczyński

## Functional Requirements
- detect anomalies in temperature measurements
    - 2 algorithms
- api to answer questions
- scalable
- high throughput

## NonFunctional Requirements
- 20k measurements per sec
- kafka messages
- REST api
- algorithms choose option in properties file
- mysql as persistent database for detected anomalies
- redis to store recent measurements, because of easy and straightforward TTL mechanism (60 sec)
- Command/Query separation
- dead letter topic

## Assumptions/Questions/Observations

No details on how sensors works, but I assume 1 sensor generate 1 measurement per second, this has implications for the application.
We got roomId, so sensor could switch rooms, so id should be based both on roomId and thermometer Id.

Note for the Client: To avoid accidental anomaly detections sensor should shoot down when switching rooms.

## Dev plan

Use CQRS separation:
- microservice for receiving measurements, detecting anomalies and storing them in database
- microservice for reading

There will probably be need scale heavily command microservice

Interacting with database and running the algorithm should take  longer then just receiving and storing the message
so there is another option, to add additional microservice:
- microservice for receiving measurements and storing them without processing in database
- microservice to process recent measurements and find anomalies with spring scheduling eg. each 1 second
- microservice for reading

This also changes the behaviour: first version with 2 microservices is checking anomaly for each message
and the second one is doing it on scheduled time based.

Because of simplicity and lack of knowledge about real world sensors I would choose option 1.

Frameworks/libraries to be used:
- spring kafka/jpa/redis
- spring webflux for REST api
- testing: jupiter/testcontainers/spring-test/restassuerd
- docker&compose to easily create containers of kafka, redis and mysql
- lombok, mapstruct, liquibase


