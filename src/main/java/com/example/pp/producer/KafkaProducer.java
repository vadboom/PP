package com.example.pp.producer;

import com.example.pp.model.MessageCreateModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, MessageCreateModel> kafkaTemplate;

    public void sendMessage(String topic, MessageCreateModel messageCreateModel, LocalTime currentTime, LocalTime deadline) {
        if (currentTime.isBefore(deadline)) {
            log.info("Отправка сообщения в тему Kafka: {}", topic);
            kafkaTemplate.send(topic, messageCreateModel);
        } else {
            log.warn("Сообщение клиенту не отправлено. Текущее время ({}) истекло после установленного крайнего срока ({}).", currentTime, deadline);
        }
    }
}
