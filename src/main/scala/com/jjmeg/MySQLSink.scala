package com.jjmeg

import java.sql.{Connection, DriverManager, PreparedStatement}

import org.apache.flink.api.scala._
import org.apache.flink.configuration.Configuration
import org.apache.flink.streaming.api.functions.sink.{RichSinkFunction, SinkFunction}
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment

object MySQLSink {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)

    /** *
     * 文件进，mysql出
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

    //    sink
    stream.addSink(new MyJDBCSink())

    env.execute("MySQL sink test")
  }
}

class MyJDBCSink() extends RichSinkFunction[SensorReading] {
  //  定义sql连接、sql语句、预编译器
  var conn: Connection = _
  var insertStmt: PreparedStatement = _
  var updateStmt: PreparedStatement = _

  //  初始化过程，创建连接和预编译语句
  override def open(parameters: Configuration): Unit = {
    super.open(parameters)
    conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "123456")
    insertStmt = conn.prepareStatement("insert into temperatures (sensor_id,temperature) VALUES (?,?)")
    updateStmt = conn.prepareStatement("update temperatures set temperature = ? where sensor_id = ?")
  }

  //  调用连接，执行sql语句
  override def invoke(value: SensorReading, context: SinkFunction.Context[_]): Unit = {
    //    执行更新语句
    updateStmt.setDouble(1, value.temperature)
    updateStmt.setString(2, value.id)
    updateStmt.execute()

    //    如果未更新成功，走插入语句
    if (updateStmt.getUpdateCount == 0) {
      insertStmt.setString(1, value.id)
      insertStmt.setDouble(2, value.temperature)
      insertStmt.execute()
    }
  }

  //  关闭时做清理工作
  override def close(): Unit = {
    insertStmt.close()
    updateStmt.close()
    conn.close()
  }
}