import com.alibaba.fastjson.JSON;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaUtil {
    public static final String broker_list = "localhost:9092";
    public static final String topic = "metric";


    public static void writeToKafka() {
        Properties props = new Properties();
        props.put("bootstrap.servers", broker_list);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); //key 序列化
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); //value 序列化

        KafkaProducer producer = new KafkaProducer<String, String>(props);

        Map<String, String> tags = new HashMap<String, String>();

        tags.put("hello", "world");

        ProducerRecord record = new ProducerRecord<String, String>(topic, null, null, JSON.toJSONString(tags));
        producer.send(record);
        System.out.println("发送数据: " + JSON.toJSONString(tags));

        producer.flush();
    }

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            Thread.sleep(300);
            writeToKafka();
        }
    }
}
