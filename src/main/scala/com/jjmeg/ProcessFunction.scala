package com.jjmeg

import akka.japi.Option.Some
import org.apache.flink.api.common.functions.{RichFlatMapFunction, RuntimeContext}
import org.apache.flink.api.common.state
import org.apache.flink.api.common.state.{ValueState, ValueStateDescriptor}
import org.apache.flink.streaming.api.functions.{KeyedProcessFunction, ProcessFunction}
import org.apache.flink.util.Collector
import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor
import org.apache.flink.streaming.api.scala.{DataStream, OutputTag, StreamExecutionEnvironment}
import org.apache.flink.streaming.api.windowing.time.Time

object ProcessFunction {
  def main(args: Array[String]): Unit = {
    //    创建批处理执行环境
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    val stream: DataStream[String] = env.socketTextStream("localhost", 7777)

    //    逐一取每条数据处理，打散之后进行word count
    val dataStream = stream.map(data => {
      val dataArray = data.split(",")
      SensorReading(dataArray(0).trim, dataArray(1).trim.toLong, dataArray(2).trim.toDouble)
    })
      .assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor[SensorReading](Time.seconds(1)) {
        override def extractTimestamp(t: SensorReading): Long = t.timestamp * 1000
      })

    val processStream = dataStream.keyBy(_.id)
      .process(new TempChangeAlert(10.0))

    val processStream2 = dataStream.keyBy(_.id)
      .flatMap(new FlatMapTempChangeAlert(10.0))

    //    R：输出类型， S：状态
    val processStream3 = dataStream.keyBy(_.id)
      .flatMapWithState[(String, Double, Double), Double] {
        //      如果没有状态，也就是没数据来过，那就将当前温度值存入状态
        case (input: SensorReading, None) => (List.empty, Some(input.temperature))
        //        如果有状态，应该比较上次的状态
        case (input: SensorReading, lastTemp) =>
          val diff = (input.temperature - lastTemp.get).abs
          if (diff > 10) {
            (List((input.id, lastTemp.get, input.temperature)), Some(input.temperature))
          } else {
            (List.empty, Some(input.temperature))
          }

      }

    env.execute()
  }


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


class TempIncreAlert() extends KeyedProcessFunction[String, SensorReading, String] {
  def processElement(value: SensorReading, ctx: KeyedProcessFunction[String, SensorReading, String]#Context, out: Collector[String]) {
    // 先定义一个状态，用来保存上一个数据的温度值
    lazy val lastTemp: ValueState[Double] = getRuntimeContext.getState(new ValueStateDescriptor[Double]("lastTime", classOf[Double]))
    // 定义一个状态，用来保存定时器的时间戳
    lazy val currentTimer: ValueState[Long] = getRuntimeContext.getState(new ValueStateDescriptor[Long]("currentTimer", classOf[Long]))

    // 先取出上一个温度值
    val preTemp = lastTemp.value()

    // 状态更新
    lastTemp.update(value.temperature)

    val currentTimerTs = currentTimer.value()

    // 温度上升且没有设过定时器，则注册定时器
    if (value.temperature > preTemp && currentTimerTs == 0) {
      // 定时器时间戳
      val timeTs = ctx.timerService().currentProcessingTime() + 1000L
      ctx.timerService().registerProcessingTimeTimer(timeTs)
      currentTimer.update(timeTs)
    } else if (preTemp > value.temperature || preTemp == 0) {
      // 如果温度下降或是第一条数据，删除定时器并清空状态
      ctx.timerService().deleteProcessingTimeTimer(currentTimerTs)
      currentTimer.clear()
    }
  }
}


class FreezingAlert() extends ProcessFunction[SensorReading, SensorReading] {
  // 主输出流
  lazy val alertOutput: OutputTag[String] = new OutputTag[String]("freezing alert")

  override def processElement(value: SensorReading, ctx: ProcessFunction[SensorReading, SensorReading]#Context, out: Collector[SensorReading]): Unit = {
    if (value.temperature < 32.0) {
      // 侧流
      ctx.output(alertOutput, "FreezingAlert for " + value.id)
    } else {
      // 主流
      out.collect(value)
    }
  }
}
