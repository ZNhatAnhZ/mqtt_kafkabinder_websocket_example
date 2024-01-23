package org.mqttclient.mqttclient.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebSocketConfig {
    @Autowired
    MqttClient mqttClientBean;
    public static Map<String, WebSocketSession> webSocketSessionMap = new HashMap<>();
    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/path", new MyWebSocketHandler());
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }

    public class MyWebSocketHandler implements WebSocketHandler {

        @Override
        public Mono<Void> handle(WebSocketSession session) {
//            if (!webSocketSessionMap.containsKey(session.getId())) {
//                webSocketSessionMap.put(session.getId(), session);
//            }

            Flux<Void> output1 = session.receive()
                    .doOnEach(message -> System.out.println("signal: " + message))
                    .doOnNext(message -> System.out.println("This is the message from the client socket to server " + message.getPayloadAsText()))
                    .concatMap(message -> {
                        try {
                            mqttClientBean.publish("mqtt", new MqttMessage(message.getPayloadAsText().getBytes()));
                        } catch (MqttException e) {
                            return Flux.error(new RuntimeException(e));
                        }
                        return Mono.just(true);
                    })
                    .map(value -> Mono.just(session.textMessage("server sent to client: " + value)))
                    .flatMap(session::send);

            Flux<Message<?>> kafakinput = streamcloudconfig.getKafkainput();
            Mono<Void> output2 = session.send(kafakinput.map(message -> session.textMessage(message.getPayload().toString())));

            return Flux.merge(output1, output2).then();
        }
    }
}