package com.example.pp.producer;

import com.example.pp.model.MessageCreateModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalTime;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerTest {

    @Mock
    private KafkaTemplate<String, MessageCreateModel> kafkaTemplate;

    @InjectMocks
    private KafkaProducer kafkaProducer;

    @Test
    public void testSendMessage_BeforeDeadline() {
        String topic = "test-topic";
        MessageCreateModel messageCreateModel = new MessageCreateModel("1234567890", "Test message");
        LocalTime currentTime = LocalTime.of(22, 0);
        LocalTime deadline = LocalTime.of(23, 30);

        kafkaProducer.sendMessage(topic, messageCreateModel, currentTime, deadline);

        verify(kafkaTemplate, times(1)).send(topic, messageCreateModel);
    }

    @Test
    public void testSendMessage_AfterDeadline() {
        String topic = "test-topic";
        MessageCreateModel messageCreateModel = new MessageCreateModel("1234567890", "Test message");
        LocalTime currentTime = LocalTime.of(23, 31);
        LocalTime deadline = LocalTime.of(23, 30);

        kafkaProducer.sendMessage(topic, messageCreateModel, currentTime, deadline);

        verify(kafkaTemplate, never()).send(anyString(), any(MessageCreateModel.class));
    }
}