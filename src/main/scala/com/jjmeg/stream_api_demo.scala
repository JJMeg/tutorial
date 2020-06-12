package com.jjmeg

import org.apache.flink.api.java.utils.ParameterTool
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

class stream_api_demo {
  def main(args: Array[String]): Unit = {
    //    host and port config
    val params = ParameterTool.fromArgs(args)
    val host: String = params.get("host")
    print("host: ", host)
    val port: Int = params.getInt("port")
    print("port: ", port)

    //    创建执行环境，流处理执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //    数据流，接收socket文本流
    val inputStream: DataStream[String] = env.socketTextStream(host, port)

    //    逐一取每条数据处理，打散之后进行word count
    val wordcountDataStream: DataStream[(String, Int)] = inputStream.flatMap(_.split(" "))
      .filter(_ != null).startNewChain()
      .map((_, 1)).startNewChain()
      .keyBy(0)
      .sum(1)

    //    设置并行度
    wordcountDataStream.print().setParallelism(2)

    wordcountDataStream.writeAsCsv("./")

    //    启动executor
    env.execute("stream word count job")
  }
}
