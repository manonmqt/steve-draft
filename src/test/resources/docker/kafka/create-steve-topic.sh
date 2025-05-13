
#!/bin/bash

/opt/bitnami/kafka/bin/kafka-topics.sh --topic "steve-location-topic" --create --partitions 1 --replication-factor 1 --bootstrap-server steve-kafka:9092
