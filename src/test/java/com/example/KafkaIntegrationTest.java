package com.example;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.utility.DockerImageName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Disabled("Docker not available - integration test disabled")
public class KafkaIntegrationTest {
    
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.2.1"));

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TestKafkaConsumer testConsumer;

    @BeforeAll
    public static void startKafka() {
        kafka.start();
    }

    @AfterAll
    public static void stopKafka() {
        kafka.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    @Test
    void shouldSendAndReceiveMessage() throws InterruptedException {
        // Given
        String topic = "test-topic";
        String message = "Hello, Kafka!";
        
        // When
        kafkaTemplate.send(topic, message);
        
        // Then
        boolean messageReceived = testConsumer.getLatch().await(10, TimeUnit.SECONDS);
        assertTrue(messageReceived, "Message should be received within 10 seconds");
    }

    @Component
    static class TestKafkaConsumer {
        private CountDownLatch latch = new CountDownLatch(1);
        private String receivedMessage;

        @KafkaListener(topics = "test-topic", groupId = "test-group")
        public void listen(String message) {
            this.receivedMessage = message;
            latch.countDown();
        }

        public CountDownLatch getLatch() {
            return latch;
        }

        public String getReceivedMessage() {
            return receivedMessage;
        }
    }
}