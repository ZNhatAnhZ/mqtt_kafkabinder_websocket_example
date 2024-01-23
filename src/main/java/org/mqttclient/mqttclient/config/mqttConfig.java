package org.mqttclient.mqttclient.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Configuration
public class mqttConfig {
    @Bean
    public MqttClient mqttClientBean() {
        String subTopic = "mqtt";
        String content = "Hello World";
        int qos = 2;
        String broker = "tcp://localhost:1883";
        String clientId = "emqx_test";
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            MqttClient client = new MqttClient(broker, clientId, persistence);

            // MQTT connection option
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setUserName("emqx_test");
            connOpts.setPassword("emqx_test_password".toCharArray());
            // retain session
            connOpts.setCleanSession(true);

            // set callback
            client.setCallback(new OnMessageCallback());

            // establish a connection
            System.out.println("Connecting to broker: " + broker);
            client.connect(connOpts);

            System.out.println("Connected");
//            System.out.println("Publishing message: " + content);

            // Subscribe
            client.subscribe(subTopic);

            // Required parameters for message publishing
            MqttMessage message = new MqttMessage(content.getBytes());
            message.setQos(qos);
//            client.publish(pubTopic, message);
//            System.out.println("Message published");
            return client;
//            client.disconnect();
//            System.out.println("Disconnected");
//            client.close();
//            System.exit(0);
        } catch (MqttException me) {
            me.printStackTrace();
            return null;
        }
    }
}
