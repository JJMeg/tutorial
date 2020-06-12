package com.jjmeg

import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011
import org.apache.flink.streaming.connectors.kafka.internal.FlinkKafkaProducer

object sink {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    /***
     * 文件进，kafka出
     */
    val inputPath = "D:\\IntelliJ IDEA 2019.2.3\\tutorial\\src\\main\\resources\\hello.txt"
    val streamFromFile = env.readTextFile(inputPath)

    val stream = streamFromFile
      .map(
        data => {
          val dataArray = data.split(",")
          SensorReading(dataArray(0).trim, dataArray(1).trim.toLong, dataArray(2).trim.toDouble).toString //转成序列化，方便输出
        }
      )

    //    sink
    stream.addSink(new FlinkKafkaProducer011[String]("localhost:9092", "sinkTest", new SimpleStringSchema()))


    /***
     * kafka进，kafka出
     */
    env.execute("sink test")
  }
}
