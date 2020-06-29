package common.datastream.output.redis;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;


/**
 * @author hexiaoying10
 * @create 2020/06/29 20:44
 */
public class RedisSink extends RichSinkFunction<Tuple> {

    /**
     * 初始化
     * @param parameters
     * @throws Exception
     */
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }

    /**
     * 数据处理
     * @param data
     * @param context
     * @throws Exception
     */
    @Override
    public void invoke(Tuple data, Context context) throws Exception {

    }

    /**
     * 结束操作
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        super.close();
    }
}
