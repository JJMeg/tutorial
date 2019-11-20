package com.jjmeg

import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment

//流处理word count
object streamcount {
  def main(args: Array[String]): Unit = {
    //    创建执行环境，流处理执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    //    数据流，socket文本流
    val dataStream = env.socketTextStream("localhost", 7777)

    //    每条数据处理
    val wordcountDataStream = dataStream.flatMap(_.split(" "))
      .filter(_ != null)
      .map((_, 1))
      .keyBy(0)
      .sum(1)

    wordcountDataStream.print().setParallelism(2)

    //    启动executor
    env.execute("stream word count job")
  }
}
