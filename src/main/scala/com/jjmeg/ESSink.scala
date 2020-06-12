package com.jjmeg

import java.util

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch6.ElasticsearchSink
import org.apache.http.HttpHost
import org.elasticsearch.client.Requests

object ESSink {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    /** *
     * 文件进，es出
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

    val httphost = new util.ArrayList[HttpHost]()
    httphost.add(new HttpHost("localhost", 9200))

    //    创建一个essink builder
    val esBuilder = new ElasticsearchSink.Builder[SensorReading](
      httphost,
      new ElasticsearchSinkFunction[SensorReading] {
        override def process(t: SensorReading, ctx: RuntimeContext, indexer: RequestIndexer): Unit = {
          println("saving data: ", t)
          //          包装成hashmap
          val json = new util.HashMap[String, String]()
          json.put("sensor_id", t.id)
          json.put("temperature", t.temperature.toString)
          json.put("ts", t.timestamp.toString)

          //          创建index request，发送
          val indexReq = Requests.indexRequest()
            .index("sensor")
            .`type`("readingdata")
            .source(json)

          //        利用indexer发送req
          indexer.add(indexReq)

          println("data sent...")
        }
      })


    //    sink
    stream.addSink(esBuilder.build())
  }
}
