package com.flinkunbounded.mqtt;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
 
import java.util.*;
 
public class StreamingJob {
    /**
     * Skeleton for a Flink Streaming Job.
     *
     * <p>For a tutorial how to write a Flink streaming application, check the
     * tutorials and examples on the <a href="http://flink.apache.org/docs/stable/">Flink Website</a>.
     *
     * <p>To package your appliation into a JAR file for execution, run
     * 'mvn clean package' on the command line.
     *
     * <p>If you change the name of the main class (with the public static void main(String[] args))
     * method, change the respective entry in the POM.xml file (simply search for 'mainClass').
     */

    

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.setParallelism(1);

        DataStream<String> stream = env.addSource(new MqttConsumer());

        DataStream<Tuple2<String, String>> dataStream = stream.flatMap(new FlatMapFunction<String, Tuple2<String, String>>() {
            @Override
            public void flatMap(String value, Collector<Tuple2<String, String>> out) throws Exception {
                String[] str = value.split(" ");
                out.collect(Tuple2.of(str[0], str[1]));
            }
        }).setParallelism(1);
        dataStream.print();
        env.execute();
    }
}