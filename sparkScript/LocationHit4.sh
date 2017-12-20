#!/bin/bash

spark-submit \
--master spark://master:7077 \
--name LocationHit \
--class nyu.bigdata.location \
--jars lib/spark-streaming-kafka-0-10_2.11-2.1.1.jar,lib/kafka-clients-0.10.2.1.jar \
--total-executor-cores 2 \
location-4.0-SNAPSHOT.jar 
