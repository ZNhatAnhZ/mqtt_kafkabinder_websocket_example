package org.mqttclient.mqttclient;

import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class testController {
    private final MqttClient mqttClientBean;
    @PostMapping("/lmao")
    public Mono<String> lmao(@RequestBody Map<String, String> map) {
        return Mono.just(map.get("message")).flatMap(e -> {
            try {
                mqttClientBean.publish(map.get("topic"), new MqttMessage(e.getBytes()));
            } catch (MqttException ex) {
                throw new RuntimeException(ex);
            }
            return Mono.just(e);
        }).map(e -> "ok");
    }
}
