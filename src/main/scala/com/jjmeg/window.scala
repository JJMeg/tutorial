package com.jjmeg

import org.apache.flink.api.common.state.StateTtlConfig.TimeCharacteristic
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

object window {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    //    设置eventTime
    //    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)

    val inputPath = "D:\\IntelliJ IDEA 2019.2.3\\tutorial\\src\\main\\resources\\hello.txt"
    //    读到的是字符串类型
    val streamFromFile = env.readTextFile(inputPath)

    val stream: DataStream[SensorReading] = streamFromFile.map(data => {
      val dataArray = data.split(",")
      SensorReading(dataArray(0).trim, dataArray(1).trim.toLong, dataArray(2).trim.toDouble)
    }).keyBy("id")

    //    输出最小温度
    val minTempPerWindowStream = stream
      .map(data => (data.id, data.temperature))
      .keyBy(_._1)
      .timeWindow(Time.seconds(10)) //开时间窗口
      .reduce((data1, data2) => (data1._1, data2._2.min(data2._2))) // 用reduce做增量聚合
  }
}
