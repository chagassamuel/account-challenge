package br.com.itau.account.challenge.kafka;

import br.com.itau.account.challenge.kafka.exception.TopicNameEmptyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.generic.GenericRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.logging.log4j.util.Strings.isEmpty;

@Slf4j
@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, GenericRecord> kafkaTemplate;

    public void send() {
        this.send(null, "");
    }

    public ProducerRecord<String, GenericRecord> send(final GenericRecord genericRecord, final String topicName) {
        String correlationId = UUID.randomUUID().toString();
        ProducerRecord<String, GenericRecord> messageRecord = null;
        try {
            if (isEmpty(topicName)) {
                throw new TopicNameEmptyException("Topic name is empty. Check application.ym1");
            }

            List<Header> headers = createHeaders(correlationId);
            messageRecord = new ProducerRecord<>(topicName, null, null, correlationId, genericRecord, headers);
            if (genericRecord == null) {
                throw new KafkaException("Avro Generic Record is null. Verify your avro file.");
            }
            final CompletableFuture<SendResult<String, GenericRecord>> future = kafkaTemplate.send(messageRecord);
            future.join();

        } catch (KafkaException | org.apache.kafka.common.KafkaException e) {
            log.error("Error sending message", e);
        } catch (TopicNameEmptyException e) {
            log.error("Kafka topic name is blank or empty");
        }
        return messageRecord;
    }

    private List<Header> createHeaders(final String id) {
        List<Header> headers = new ArrayList<>();
        addStringHeader(headers, "correlationid", id);
        addStringHeader(headers, "datacontenttype", "application/json");
        addStringHeader(headers, "id", id);
        addStringHeader(headers, "messageversion", "1.0");
        addStringHeader(headers, "specversion", "2.1");
        addStringHeader(headers, "time", OffsetDateTime.now(ZoneOffset.UTC).toString());
        addStringHeader(headers, "transaction", id);
        return headers;
    }

    private void addStringHeader(final List<Header> headers, final String key, final String value) {
        headers.add(new RecordHeader(key, value.getBytes(UTF_8)));
    }

}
