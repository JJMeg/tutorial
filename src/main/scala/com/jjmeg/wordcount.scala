package com.jjmeg

import org.apache.flink.api.scala._

// 批处理wordcount程序
object wordcount {
  def main(args: Array[String]): Unit = {
    //    创建批处理执行环境
    val env = ExecutionEnvironment.getExecutionEnvironment

    //    从文件中读取数据
    val inputPath = "D:\\IntelliJ IDEA 2019.2.3\\tutorial\\src\\main\\resources\\hello.txt"
    val inputDataSet = env.readTextFile(inputPath)

    //    做count处理
    //    切分数据，得到word，按word做分组聚合
    val wordCountDataSet = inputDataSet.flatMap(_.split(" "))
      .map((_, 1))
      .groupBy(0)
      .sum(1)

    wordCountDataSet.print()
  }
}
