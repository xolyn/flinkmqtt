package com.flinkunbounded.mqtt;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
 

 
public class MqttConsumer extends RichParallelSourceFunction<String>{
    //存储服务
    private static MqttClient client;
    //存储订阅主题
    private static MqttTopic mqttTopic;
    //阻塞队列存储订阅的消息
    private BlockingQueue<String> queue = new ArrayBlockingQueue<String>(10);
 
    //包装连接的方法
    private void connect() throws MqttException {
        //配置连接参数
        MqttConfig mqttConfigBean = new MqttConfig("admin", "admin",
         "tcp://localhost:1883", "ee8b3b21036746d4877026709a894c77", "home/garden/fountain");
        //连接mqtt服务器
        client = new MqttClient(mqttConfigBean.getHostUrl(), mqttConfigBean.getClientId(), new MemoryPersistence());
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(false);
        options.setUserName(mqttConfigBean.getUsername());
        options.setPassword(mqttConfigBean.getPassword().toCharArray());
        options.setCleanSession(false);   //是否清除session
        // 设置超时时间
        options.setConnectionTimeout(30);
        // 设置会话心跳时间
        options.setKeepAliveInterval(20);
        try {
            String[] msgtopic = mqttConfigBean.getMsgTopic();
            //订阅消息
            int[] qos = new int[msgtopic.length];
            for (int i = 0; i < msgtopic.length; i++) {
                qos[i] = 0;
            }
            client.setCallback(new MsgCallback(client, options, msgtopic, qos){});
            client.connect(options);
            client.subscribe(msgtopic, qos);
            System.out.println("MQTT连接成功:" + mqttConfigBean.getClientId() + ":" + client);
        } catch (Exception e) {
            System.out.println("MQTT连接异常：" + e);
        }
    }
    //实现MqttCallback，内部函数可回调
    class MsgCallback implements MqttCallback{
        private MqttClient client;
        private MqttConnectOptions options;
        private String[] topic;
        private int[] qos;
 
        public MsgCallback() {
        }
 
        public MsgCallback(MqttClient client, MqttConnectOptions options, String[] topic, int[] qos) {
            this.client = client;
            this.options = options;
            this.topic = topic;
            this.qos = qos;
        }
        //连接失败回调该函数
        @Override
        public void connectionLost(Throwable throwable) {
            System.out.println("MQTT连接断开，即将重连");
            while (true) {
                try {
                    Thread.sleep(1000);
                    client.connect(options);
                    //订阅消息
                    client.subscribe(topic, qos);
                    System.out.println("MQTT重新连接成功:" + client);
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
        }
        //收到消息回调该函数
        @Override
        public void messageArrived(String s, MqttMessage message) throws Exception {
            System.out.println();
            //订阅消息字符
            String msg = new String(message.getPayload());
            byte[] bymsg = getBytesFromObject(msg);
            System.out.println("topic:" + topic);
            queue.put(msg);
 
        }
        //对象转化为字节码
        public byte[] getBytesFromObject(Serializable obj) throws Exception {
            if (obj == null) {
                return null;
            }
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            return bo.toByteArray();
        }
 
        @Override
        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
 
        }
    }



    @Override
    public void run(final SourceContext<String> ctx) throws Exception {
        connect();
        while (true){
            ctx.collect(queue.take());
        }
    }
 
    @Override
    public void cancel() {
 
    }

    public void subscribe(String topic, int qos) {
        try {
            System.out.println("topic:" + topic);
            client.subscribe(topic, qos);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
 
    public MqttClient getClient() {
        return client;
    }
 
    public void setClient(MqttClient client) {
        this.client = client;
    }
 
    public MqttTopic getMqttTopic() {
        return mqttTopic;
    }
 
    public void setMqttTopic(MqttTopic mqttTopic) {
        this.mqttTopic = mqttTopic;
    }
 
 
}