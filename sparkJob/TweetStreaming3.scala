package bigdata

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.{KafkaUtils, LocationStrategies}

/**
  * Created by dustin on 2017/12/8.
  * Compared with TweetStreaming, it has new function called Natural disaster warning
  */
object TweetStreaming3 {
  def main(args: Array[String]): Unit = {
    if (args.size != 2) {
      println("Please input args like: checkpoint kafkaTopic")
      System.exit(1)
    }

    def createStreamingContext(): StreamingContext = {
      //设置spark配置信息，启动SC
      val sparkConf = new SparkConf()

      val ssc = new StreamingContext(sparkConf, Seconds(5)) //时间窗口为10秒，每10处理一批数据
      ssc.checkpoint(args(0)) // 自己设定，设置断点检查，存储元信息。断点后继续恢复
      val sqlspark = SparkSession.builder().config(sparkConf).getOrCreate()





      //连接Kafka，配置Kafka
      val kafkaParams = Map[String, Object](
        "bootstrap.servers" -> "master:9092",
        "key.deserializer" -> classOf[StringDeserializer],
        "value.deserializer" -> classOf[StringDeserializer],
        "group.id" -> "dustin",
        "auto.offset.reset" -> "latest",
        "enable.auto.commit" -> (false: java.lang.Boolean)
      )
      val topic = Array(args(1)) //设置kafka topic

      var positiveRDD = ssc.sparkContext.textFile("/dustin/nyu/dataset/positive.txt")

      var negativeRDD = ssc.sparkContext.textFile("/dustin/nyu/dataset/negative.txt")

      var warningRDD = ssc.sparkContext.textFile("/dustin/nyu/dataset/warning.txt")

      var positive = positiveRDD.collect();
      var negative = negativeRDD.collect();
      var warning = warningRDD.collect();

      val broadcastData = ssc.sparkContext.broadcast(positive,negative,warning)



      val lines = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferBrokers, Subscribe[String, String](topic, kafkaParams))


      lines.foreachRDD(RDD => {

        RDD.foreachPartition( partition =>tweetsAnalysis(partition,broadcastData) )})

      return ssc
    }
    //Check SQL DATE Before start spark streaming
    initSentimentCountSQL

    val ssc = StreamingContext.getOrCreate(args(0), createStreamingContext _)
    ssc.start()
    ssc.awaitTermination()
  }
  def tweetsAnalysis(tweetsSet:Iterator[ConsumerRecord[String, String]], broadcastData :Broadcast[(Array[String], Array[String], Array[String])]):Unit = {
    val positiveWords = broadcastData.value._1
    val negativeWords = broadcastData.value._2
    val warningWords = broadcastData.value._3


    var positiveCount = 0;
    var negativeCount = 0;
    var other = 0;



    tweetsSet.foreach(tweet =>{
      var warningFind:Boolean = false
      //val array: Array[String] = tweet.value().split("<*****>")
      var value = tweet.value()
      val arrData: Array[String] = value.split("<\\*\\*\\*\\*\\*>")
      val content:  Array[String] = arrData(0).split(" ")
      for(word <- content){
        // First if use for tweet Sentiment Analysis
        if (positiveWords.contains(word)) positiveCount += 1
        else if (negativeWords.contains(word)) negativeCount += 1
        else other += 1

        // Second use for tweet Natural Disaster Warning
        if (!warningFind){
          if (warningWords.contains(word.toLowerCase)){
                checkWarningSQL(word.toLowerCase(),arrData(4),arrData(5),arrData(7).toDouble,arrData(6).toDouble)
                warningFind = true
          }
        }

      }

    })
    var res = s"This partition has positive words: $positiveCount and negative words: $negativeCount, other words: $other, ${Calendar.getInstance().getTime}"
    println(res)
    updateSentimentCountSQL(negativeCount,positiveCount,other)

  }

  def checkWarningSQL(warningType:String,city:String,state:String,lat:Double,lon:Double):Unit={
    var time:String = getNowDate()
    var conn: Connection = null
    var ps: PreparedStatement = null
    val checksql = "select city,lat,lon,count from NDWarning where time = ? and warningtype = ? and state= ? ";
    val insertsql = "insert into NDWarning(time, warningtype, city, state,lat,lon,count) values(?,?,?,?,?,?,?)";
    val updatesql = "UPDATE NDWarning SET count= ? WHERE time= ? and warningtype= ? and city=? and state=? and lat=? and lon=?";
    try {
      conn = DriverManager.getConnection("jdbc:mysql://master/dustin", "root", "root")
      ps = conn.prepareStatement(checksql)
      ps.setString(1,time)
      ps.setString(2,warningType)
      ps.setString(3,state)
      var res: ResultSet = ps.executeQuery()

      def insertInitData(time:String,warningType:String,city:String,state:String,lat:Double,lon:Double,count:Int):Unit = {
        try {
          var insertPs = conn.prepareStatement(insertsql)
          insertPs.setString(1,time)
          insertPs.setString(2,warningType)
          insertPs.setString(3,city)
          insertPs.setString(4,state)
          insertPs.setDouble(5,lat)
          insertPs.setDouble(6,lon)
          insertPs.setInt(7,count)
          insertPs.execute()
        }
        catch {
          case e: Exception => println("insert: "+e)
        }
        finally {
          if (ps != null) {
            ps.close()
          }
        }

      }

      def updateData(time:String,warningType:String,city:String,state:String,lat:Double,lon:Double,count:Int):Unit = {

        try{
          var updatePs = conn.prepareStatement(updatesql)
          updatePs.setInt(1,count)
          updatePs.setString(2,time)
          updatePs.setString(3,warningType)
          updatePs.setString(4,city)
          updatePs.setString(5,state)
          updatePs.setDouble(6,lat)
          updatePs.setDouble(7,lon)
          updatePs.execute()
        }
        catch {
          case e: Exception => println("update: "+e)
        }
        finally {
          if (ps != null) {
            ps.close()
          }
        }

      }
            //(lat,lon,count,distance)
        var nearestPost: (String,Double, Double, Int, Double)= null;
        // Find the nearest post in the dataSet
        while (res.next()){
          // calculate distance
          val distance = checkDistance(lat,lon,res.getDouble("lat"),res.getDouble("lon"))

          println(s"The distance is: $distance")

          var tmp = (res.getString("city"),res.getDouble("lat"),res.getDouble("lon"),res.getInt("count"),distance)
          if(distance < 50){
            //if(nearestPost == null) nearestPost = tmp
            //else if(nearestPost._4 > tmp._4) nearestPost = tmp
            nearestPost = tmp
          }

        }
        // Every post is too far from this post, so we think it should be a new warning
        if (nearestPost == null) {
          print("every data can't satisfy my location, insert new one")
          insertInitData(time,warningType,city,state,lat,lon,1)
        }
        else {
          //  Warning post count +1
          updateData(time,warningType,nearestPost._1,state,nearestPost._2,nearestPost._3,nearestPost._4+1)
        }


    }
    catch {
      case e: Exception => println("search: "+e)
    }finally {
      if (ps != null) {
        ps.close()
      }
      if (conn != null) {
        conn.close()
      }
    }

  }


  def checkDistance(a_latitude:Double,a_longitude:Double,b_latitude:Double,b_longitude:Double): Double={
    val centerLat = b_latitude
    val centerLon = b_longitude
    def rad(degree:Double): Double={
      var res = degree* Math.PI/180
      return res
    }
    var distance = math.acos(math.sin(rad(a_latitude))*math.sin(rad(centerLat)) + math.cos(rad(a_latitude))*Math.cos(rad(centerLat))*Math.cos(rad(a_longitude)- rad(centerLon)))*6371
    return distance
  }


  def updateSentimentCountSQL(negative:Int,positive:Int,other:Int): Unit = {
    val time :String = getNowDate()
    var conn: Connection = null
    var ps: PreparedStatement = null
    val sql = "UPDATE tweetCount SET negative=?+negative,positive=?+positive,other=?+other WHERE time= ?"

    try {
      conn = DriverManager.getConnection("jdbc:mysql://master/dustin", "root", "root")
      ps = conn.prepareStatement(sql)
      ps.setInt(1,negative)
      ps.setInt(2, positive)
      ps.setInt(3,other)
      ps.setString(4,time)
      ps.executeUpdate()

    } catch {
      case e: Exception => println(e)
    } finally {
      if (ps != null) {
        ps.close()
      }
      if (conn != null) {
        conn.close()
      }
    }
  }

  def initSentimentCountSQL():Unit = {
    val currTime = getNowDate()
    var sqlCheck = "select * from tweetCount where time = ?";
    var sqlInsert = "insert into tweetCount(time, positive, negative, other) values(?,?,?,?)"
    var conn: Connection = null
    var ps: PreparedStatement = null
    try {
      conn = DriverManager.getConnection("jdbc:mysql://master/dustin", "root", "root")
      ps = conn.prepareStatement(sqlCheck);
      ps.setString(1,currTime)
      var res: ResultSet = ps.executeQuery()
      if (!res.first()){
        try {
          // if there isn;t initial data, try to insert one
          ps = conn.prepareStatement(sqlInsert)
          ps.setString(1,currTime)
          ps.setInt(2,0)
          ps.setInt(3,0)
          ps.setInt(4,0)
          ps.execute()
        }
        catch {
          case e: Exception => println(e)
        }

      }
    }
    catch {
      case e: Exception => println(e)
    }
    finally {
      if (ps != null) {
        ps.close()
      }
      if (conn != null) {
        conn.close()
      }
    }
  }

  def getNowDate():String={
    var now:Date = new Date()
    var  dateFormat:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    var hehe = dateFormat.format( now )
    hehe
  }
}
