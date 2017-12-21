#!/bin/bash

spark-submit \
--master spark://master:7077 \
--name dustin \
--class bigdata.TweetStreaming3 \
--total-executor-cores 2 \
--jars lib/spark-streaming-kafka-0-10_2.11-2.1.1.jar,lib/kafka-clients-0.10.2.1.jar \
NYU-Bigdata-1.0-SNAPSHOT.jar  hdfs://cluster/dustin/nyu/checkpoint twitterstream
