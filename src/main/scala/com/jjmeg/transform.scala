package com.jjmeg

//没有加隐式转换

import org.apache.flink.api.common.functions.{FilterFunction, RichMapFunction}
import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

object transform {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    /** *
     * 基本算子和简单聚合算子
     */

    //    1. map: 做数据转换，eg：x=x*2
    val inputPath = "D:\\IntelliJ IDEA 2019.2.3\\tutorial\\src\\main\\resources\\hello.txt"
    //    读到的是字符串类型
    val streamFromFile = env.readTextFile(inputPath)

    val stream: DataStream[SensorReading] = streamFromFile.map(data => {
      val dataArray = data.split(",")
      SensorReading(dataArray(0).trim, dataArray(1).trim.toLong, dataArray(2).trim.toDouble)
    }).keyBy("id")
      //      输出当前最新温度+10，时间戳上次+1
      .reduce((x, y) => SensorReading(x.id, x.timestamp + 1, y.temperature + 10))

    //      .keyBy("id")
    //      .sum("temperature")


    //      .keyBy(0)
    //      .sum(2)
    //      .setParallelism(1)


    //    2. flatMap：数据打散
    //    3. Filter：过滤筛选
    //    4. KeyBy：分组、分区操作
    //    5. 聚合滚动算子 Rolling Aggregation，必须keyby以后才能做
    //    这是针对print做线程限制

    /** *
     * 多流转换算子
     */
    val splitStream = stream.split(data => {
      if (data.temperature > 30) Seq("high") else Seq("low")
    })
    val high = splitStream.select("high")
    val low = splitStream.select("low")
    val all = splitStream.select("high", "low")

    //    high.print("high: ").setParallelism(1)
    //    low.print("low: ").setParallelism(1)
    //    all.print("all: ").setParallelism(1)

    /**
     * 合并两条流
     */
    //    val warning = high.map(data => (data.id, data.temperature))
    //    val connectionStream = warning.connect(low)
    //    val coMapStream = connectionStream.map(
    //      warningData => (warningData._1, warningData._2, "warining"),
    //      lowData => (lowData.id, "healthy"))
    //
    //    coMapStream.print()

    /** **
     * 合并多条流
     */
    //    val unionStream = high.union(low, all)
    //    unionStream.print("union: ").setParallelism(1)

    /** *
     * 函数类
     */
    stream.filter(new MyFilter()).print()


    env.execute("transform test")
  }
}

class MyFilter() extends FilterFunction[SensorReading] {
  override def filter(value: SensorReading): Boolean = {
    value.id.startsWith("sensor_1")
  }
}

class MyMapFunciton() extends RichMapFunction[SensorReading, String] {
  override def map(in: SensorReading): String = {
    in.id
  }
}