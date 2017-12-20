package nyu.bigdata

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{FloatType, LongType, StructType}
import org.apache.spark.sql.{Row, SaveMode, SparkSession}
import org.apache.spark.streaming.kafka010.ConsumerStrategies._
import org.apache.spark.streaming.kafka010.{KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}



/**
  * Created by sulei on 12/3/17.
  */
object location {
  def main(args: Array[String]) {
    val conf = new SparkConf()
    val ssc = new StreamingContext(conf, Seconds(10))
    ssc.checkpoint("hdfs://cluster/checkpoint")

    val kafkaParams = Map[String,Object] (
      "bootstrap.servers" -> "master:9092",
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> "location",
      "auto.offset.reset" -> "latest",
      "enable.auto.commit"-> (false:java.lang.Boolean)
    )


    val kafkaTopics = Array("twitterstream")
    val stream = KafkaUtils.createDirectStream[String, String](ssc, LocationStrategies.PreferBrokers, Subscribe[String, String](kafkaTopics, kafkaParams))
    val dstream = stream.map(c => (c.key(),c.value()))

    def process(rdd:RDD[(String, String)]): RDD[(String, String)] = {
      val lines = rdd.map(_._2)
      val tweets = lines.map(_.split("<\\*\\*\\*\\*\\*>"))
      val filteredTweets = tweets.filter(tweet => tweet.length == 8)
      val USATweets = filteredTweets.filter(tweet => (tweet(3)=="United States" || tweet(3) == "USA"))
      val geoDStream = USATweets.map(tweet => (tweet(7),tweet(6)))
      return geoDStream
    }

    val geoDStream = dstream.transform { rdd => process(rdd)}

    def updateRunningSum(values: Seq[Long], state: Option[Long]) = {
      Some(state.getOrElse(0L) + values.size)
    }

    val geoCountDStream = geoDStream.map(g => (g, 1L) ).updateStateByKey(updateRunningSum _)

    geoCountDStream.foreachRDD(rdd => {
      if (!rdd.isEmpty) {
        val rowRDD = rdd.map(attributes => Row(attributes._1._1.toFloat, attributes._1._2.toFloat,attributes._2))

        val schema = new StructType()
          .add("lat", FloatType)
          .add("lng", FloatType)
          .add("count", LongType)
        val spark = SparkSession
          .builder
          .getOrCreate()
        val df = spark.createDataFrame(rowRDD, schema)


        df.collect.foreach(println)

        df.write.mode(SaveMode.Overwrite).json("hdfs://cluster/locationOutput/test")


        //create properties object
        val prop = new java.util.Properties
        prop.setProperty("driver", "com.mysql.jdbc.Driver")
        prop.setProperty("user", "root")
        prop.setProperty("password", "root")

        //jdbc mysql url - destination database is named "data"
        val url = "jdbc:mysql://master:3306/dustin"

        //destination database table
        val table = "location"

        //write data from spark dataframe to database
        df.write.mode(SaveMode.Overwrite).jdbc(url, table, prop)
      }
    })

    ssc.start()             // Start the computation
    ssc.awaitTermination()  // Wait for the computation to terminate
  }
}
