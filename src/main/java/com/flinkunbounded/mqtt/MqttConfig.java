package com.flinkunbounded.mqtt;

import java.io.Serializable;
//该类需要实现序列化所以必须实现Serializable接口
public class MqttConfig implements Serializable {
 
    public MqttConfig(String username, String password, String hostUrl, String clientId, String msgTopic) {
        this.username = username;
        this.password = password;
        this.hostUrl = hostUrl;
        this.clientId = clientId;
        this.msgTopic = msgTopic;
    }
    //连接名称
    private String username;
    //连接密码
    private String password;
    //ip地址以及端口号
    private String hostUrl;
    //服务器ID注意不能与其他连接重复，否则会连接失败
    private String clientId;
    //订阅的主题
    private String msgTopic;
 
 
    //获得用户名
    public String getUsername() {
        return username;
    }
    //获得密码
    public String getPassword() {
        return password;
    }
    //获得客户端id
    public String getClientId() {
        return clientId;
    }
    //获得服务端url
    public String getHostUrl() {
        return hostUrl;
    }
    //获得订阅
    public String[] getMsgTopic() {
        String[] topic = msgTopic.split(",");
        return topic;
    }
 
}