package org.example.steammatchmakingservice.config.kafka.sender;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class KakfaMessageSender {

    private final KafkaTemplate<String, String> kafkaTemplateString;
    private final KafkaTemplate<byte[], byte[]> kafkaTemplateByte;
    private final ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate;

    public KakfaMessageSender(KafkaTemplate<String, String> kafkaTemplateString,
                              KafkaTemplate<byte[], byte[]> kafkaTemplateByte,
                              ReplyingKafkaTemplate<String, String, String> replyKafkaTemplate) {
        this.kafkaTemplateString = kafkaTemplateString;
        this.kafkaTemplateByte = kafkaTemplateByte;
        this.replyKafkaTemplate = replyKafkaTemplate;
    }


    public void send(String requestTopic, String dataJson) {
        /* send to broker */
        CompletableFuture<SendResult<String, String>> future = kafkaTemplateString.send(requestTopic, dataJson);
        future.whenCompleteAsync((result, ex) -> {
            if (ex == null) {
                System.out.println("Success[1]: " + result);
            } else {
                System.out.println("Error[1]: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }


    public void sendWithHeaders(String requestTopic, List<Header> headers, String dataJson) {
        /* send to broker */
        ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, null, "send", dataJson, headers);
        CompletableFuture<SendResult<String, String>> future = kafkaTemplateString.send(record);
        future.whenCompleteAsync((result, ex) -> {
            if (ex == null) {
                System.out.println("Success[1]: " + result);
            } else {
                System.out.println("Error[1]: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    public void sendWithReply(String requestTopic, String replyTopic, List<Header> headers,  String dataJson) {

        ProducerRecord<String, String> record = new ProducerRecord<>(requestTopic, null, "send-and-reply", dataJson, headers);
        record.headers().add(new RecordHeader(KafkaHeaders.REPLY_TOPIC, replyTopic.getBytes()));

        RequestReplyFuture<String, String, String> replyFuture = replyKafkaTemplate.sendAndReceive(record, Duration.ofSeconds(3600));

        /* send to broker */
        CompletableFuture<SendResult<String, String>> future = replyFuture.getSendFuture();
        future.whenCompleteAsync((result, ex) -> {
            if(ex == null) {
                System.out.println("Success[2]: " + result.toString());

            } else {
                System.out.println("Error[2]: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        /* reply from after listen */
        replyFuture.whenCompleteAsync((consumerRecord, ex) -> {
            if(ex == null) {
                System.out.println("Success[2]:" + consumerRecord.value());
            } else {
                System.out.println("Error[2]: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    // Method to send file bytes with headers
    public void sendWithHeaderFileBytes(byte[] bytes, String tableName, String topic, List<Header> additionalHeaders) {
        List<Header> headers = new ArrayList<>(additionalHeaders);
        headers.add(new RecordHeader("table_name", tableName.getBytes(StandardCharsets.UTF_8)));

        ProducerRecord<byte[], byte[]> record = new ProducerRecord<>(topic, null, "byte_files".getBytes(), bytes, headers);

        CompletableFuture<SendResult<byte[], byte[]>> future = kafkaTemplateByte.send(record);
        future.whenCompleteAsync((result, ex) -> {
            if (ex == null) {
                System.out.println("Success: " + result);
            } else {
                System.err.println("Kafka send error: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }


    public void sendFileBytes(byte[] bytes, String topic) {
        /* send to broker */
        CompletableFuture<SendResult<byte[], byte[]>> future = kafkaTemplateByte.send(topic, bytes);
        future.whenCompleteAsync((result, ex) -> {
            if (ex == null) {
                System.out.println("Success[1]: " + result);
            } else {
                System.out.println("Error[1]: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }
}
