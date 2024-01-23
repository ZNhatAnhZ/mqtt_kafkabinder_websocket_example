package org.mqttclient.mqttclient.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.mqttclient.mqttclient.config.WebSocketConfig.webSocketSessionMap;

@Configuration
public class streamcloudconfig {
    public static final Sinks.Many<Message<?>> sink =
            Sinks.many().multicast().onBackpressureBuffer();
    public static final Sinks.Many<Message<?>> sink_input =
            Sinks.many().multicast().onBackpressureBuffer();

    public static void emitMessageToSink(byte[] message) {
        sink.tryEmitNext(MessageBuilder.withPayload(message).build());
    }

    public static Flux<Message<?>> getKafkainput() {
        return sink_input.asFlux();
    }

    @Bean
    public Consumer<Message<String>> lmaoinput() {
        return stringMessage -> {
//            Flux.fromStream(webSocketSessionMap.values().stream().filter(WebSocketSession::isOpen))
//                    .flatMap(webSocketSession -> webSocketSession.send(Mono.just(webSocketSession.textMessage(stringMessage.getPayload()))))
//                    .subscribeOn(Schedulers.boundedElastic())
//                    .subscribe();
            sink_input.tryEmitNext(stringMessage);
            System.out.println(stringMessage.getPayload());
        };
    }

    @Bean
    public Supplier<Flux<?>> lmaooutput() {
        return sink::asFlux;
    }
}
