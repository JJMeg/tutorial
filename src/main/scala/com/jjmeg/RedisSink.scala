package com.jjmeg

import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.redis.RedisSink
import org.apache.flink.streaming.connectors.redis.common.config.FlinkJedisPoolConfig
import org.apache.flink.streaming.connectors.redis.common.mapper.{RedisCommand, RedisCommandDescription, RedisMapper}

object RedisSink {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    /** *
     * 文件进，redis出
     */
    val inputPath = "D:\\IntelliJ IDEA 2019.2.3\\tutorial\\src\\main\\resources\\hello.txt"
    val streamFromFile = env.readTextFile(inputPath)

    val stream = streamFromFile
      .map(
        data => {
          val dataArray = data.split(",")
          SensorReading(dataArray(0).trim, dataArray(1).trim.toLong, dataArray(2).trim.toDouble)
        }
      )


    //    sink
    val conf = new FlinkJedisPoolConfig.Builder()
      .setHost("localhost")
      .setPort(6379)
      .build()

    stream.addSink(new RedisSink(conf, new MyRedisMapper))
  }
}

class MyRedisMapper() extends RedisMapper[SensorReading] {
  //  定义保存数据到redis的命令
  override def getCommandDescription: RedisCommandDescription = {
    //保存成hset key field value
    new RedisCommandDescription(RedisCommand.HSET, "sensor_temperature")
  }

  //  定义保存到redis的key
  override def getKeyFromData(t: SensorReading): String = t.id.toString

  //  定义保存到redis的key
  override def getValueFromData(t: SensorReading): String = t.temperature.toString
}