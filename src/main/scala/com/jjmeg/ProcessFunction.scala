package com.jjmeg

import org.apache.flink.api.common.functions.RichFlatMapFunction
import org.apache.flink.api.common.state
import org.apache.flink.api.common.state.ValueStateDescriptor
import org.apache.flink.streaming.api.functions.KeyedProcessFunction
import org.apache.flink.util.Collector
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.scala.DataStream

object ProcessFunction {
  //    创建批处理执行环境
  val env = ExecutionEnvironment.getExecutionEnvironment

  val inputPath = "D:\\IntelliJ IDEA 2019.2.3\\tutorial\\src\\main\\resources\\hello.txt"
  val streamFromFile = env.readTextFile(inputPath)

  val stream = streamFromFile
    .map(
      data => {
        val dataArray = data.split(",")
        SensorReading(dataArray(0).trim, dataArray(1).trim.toLong, dataArray(2).trim.toDouble)
      }
    )
}


/** *
  * 有状态
  *
  * @param threshold
  */
class TempChangeAlert(threshold: Double) extends KeyedProcessFunction[String, SensorReading, (String, Double, Double)] {
  //  定义一个状态变量，保存上次的温度值
  lazy val lastTempState: state.ValueState[Double] = getRuntimeContext.getState(new ValueStateDescriptor[Double]("lastTemp", classOf[Double]))

  override def processElement(value: SensorReading, context: KeyedProcessFunction[String, SensorReading, (String, Double, Double)]#Context, out: Collector[(String, Double, Double)]): Unit = {
    //  获取上次温度值
    val lastTemp = lastTempState.value()
    //    比较两次温度
    val diff = (value.temperature - lastTemp).abs
    if (diff > 10) {
      out.collect((value.id, lastTemp, value.temperature))
    }

    lastTempState.update(value.temperature)
  }

}

/**
  * 无状态
  *
  * @param threshold
  */

class FlatMapTempChangeAlert(threshold: Double) extends RichFlatMapFunction[SensorReading, (String, Double, Double)] {
  private var lastTempState: state.ValueState[Double] = _

  override def open(parameters: Configuration): Unit = {
    //    初始化的时候生命state变量
    lastTempState = getRuntimeContext.getState(new ValueStateDescriptor[Double]("lastTemp", classOf[Double]))
  }

  override def flatMap(value: SensorReading, out: Collector[(String, Double, Double)]): Unit = {
    //  获取上次温度值
    val lastTemp = lastTempState.value()
    //    比较两次温度
    val diff = (value.temperature - lastTemp).abs
    if (diff > 10) {
      out.collect((value.id, lastTemp, value.temperature))
    }

    lastTempState.update(value.temperature)
  }
}
