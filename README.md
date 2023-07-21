# flinkmqtt
## **Still updating!**

A MQTT command receiver for Apache Flink, using MQTT.fx to perform test

### Step:
1. Download `mqtt.fx 1.7.1`
2. Install mosquitto server on your virtual machine or server by linux command:
    ```
    $ sudo apt install -y mosquitto
    ```
3. Use this command to check the status of mosquitto server, where it     should be both `active` and `loaded`
    ```
    $ sudo systemctl status mosquitto
    ```
4. After installation, mosquitto should be automatically started. If not use this command to manually start it:
    ```
    $ sudo systemctl start mosquitto
    ```
    Moreover, use this command to stop it:
    ```
    $ sudo systemctl stop mosquitto
    ```
    Or restart it:
    ```
    $ sudo systemctl restart mosquitto
    ```
5. Install Mosquitto Clients using this command:
    ```
    $ sudo apt install -y mosquitto-clients
    ```
6. A **TOPIC** is like a relative path but in IoT setting, like:
    `home/living_room/TV`, `home/bathroom/toilet`, `home/garden/fountain`, etc. You can subscribe a topic by command line:
    ```
    $ mosquitto_sub -t [TOPIC NAME]
    ```
    For example:
    ```
    $ mosquitto_sub -t "home/living_room/TV"
    ```
7. You can push notifications using command, for example, set the `TV` in the `living room` `ON`:
    ```
    $ mosquitto_pub -m "ON" -t "home/living_room/TV"
    ```
    ***Since we use `mqtt.fx`, it is not necessary to use command to subscribe and push commands.***
8. Open `mqtt.fx`, click the gear icon, connect to `localhost: 1883`, we choose 1883 because it does not need any authentication to connect. Then specify the `username`, `password`, `hostname`,`clientID` and `msgTopic` properties for object `MqttConfig` in line `24` in `MqttConsumer.java`

    *The values of `username`, `password`, `hostname` and `msgTopic` should be the **SAME** as those in `mqtt.fx` configuration, while `clientID` should be **DIFFERENT** with the one in `mqtt.fx`! Or you will get frequently kicked out whenever you try to connect to both clients.*

9. Start both `mqtt.fx` client and the java program, pushing notifications on `mqtt.fx` and you should be able to see the commands on the terminal where you run java program.
