package com.jjmeg

import java.util.Properties

import org.apache.flink.streaming.api.functions.source.SourceFunction

import scala.util.Random

//没有加隐式转换
import org.apache.flink.api.scala._
import org.apache.flink.api.common.serialization.SimpleStringSchema

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011


// 进来的数据要包装成的样例类
case class SensorReading(id: String, timestamp: Long, temperature: Double)

object source {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //    从不同的源获取数据
    //    1. 有界流：从自定义的集合中读取数据
    val stream1 = env.fromCollection(List(
      SensorReading("sensor_1", 1547718199, 35.6),
      SensorReading("sensor_2", 1547718239, 34.6),
      SensorReading("sensor_3", 1547718249, 32.6),
      SensorReading("sensor_4", 1547718259, 36.6),
      SensorReading("sensor_5", 1547718269, 38.6)
    ))

    stream1.print("stream1: ").setParallelism(1)

    //    2. 有界流：从文件中读取
    val inputPath = "D:\\IntelliJ IDEA 2019.2.3\\tutorial\\src\\main\\resources\\hello.txt"
    val stream2 = env.readTextFile(inputPath)

    //    只做了读取，未包装成类
    stream2.print("stream2: ").setParallelism(1)


    //    3. 无界流：从kafka读取
    //    如何和kafka做交互？
    //    使用官方连接器
    //    val properties = new Properties()
    //    properties.setProperty("bootstrap.servers", "localhost:9092")
    //    properties.setProperty("group.id", "consumer-group")
    //    //     ....
    //
    //    val stream3 = env.addSource(new FlinkKafkaConsumer011[String]("sensor", new SimpleStringSchema(), properties))
    //    stream3.print("stream3: ").setParallelism(1)

    //    4. 自定义source
    val stream4 = env.addSource(new SensorSource())
    stream4.print("stream4: ").setParallelism(1)
    env.execute("source_test")
  }
}


class SensorSource() extends SourceFunction[SensorReading] {
  //  flag表示数据是否正常运行
  var running: Boolean = true

  override def cancel(): Unit = {
    running = false
  }

  override def run(sourceContext: SourceFunction.SourceContext[SensorReading]): Unit = {
    //  init 随机数发生器
    var rand = new Random()

    //   初始化定义一组传感器温度数据
    var curTem = 1.to(10).map(
      i => ("sensor_" + i, 60 + rand.nextGaussian() * 20)
    )

    //    产生数据流
    while (running) {
      //      更新温度值
      curTem = curTem.map(
        t => (t._1, t._2 + rand.nextGaussian())
      )

      //      获取当前时间戳
      val curTime = System.currentTimeMillis()
      curTem.foreach(
        //        利用上下文获取数据并发送
        t => sourceContext.collect(SensorReading(t._1, curTime, t._2))
      )

      Thread.sleep(1000)
    }
  }
}