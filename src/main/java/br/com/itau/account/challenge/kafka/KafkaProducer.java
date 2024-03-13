package br.com.itau.account.challenge.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaProducer {

    public void send(final Object object) {
        log.warn("Sending failed request of BACEN to kafka topic");
        // send to topic
    }

}
