package br.com.itau.account.challenge.integration.bacen.service;

import br.com.itau.account.challenge.integration.bacen.client.BacenClient;
import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.integration.bacen.domain.response.BacenResponse;
import br.com.itau.account.challenge.kafka.KafkaProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BacenService {

    private final BacenClient bacenClient;
    private final KafkaProducer kafkaProducer;

    @CircuitBreaker(name = "bacenCB", fallbackMethod = "notifyTransferFallback")
    public BacenResponse notifyTransfer(final BacenRequest request) {
        return bacenClient.notifyTransfer(request).getBody();
    }

    private BacenResponse notifyTransferFallback(final BacenRequest request, final Throwable e) {
        kafkaProducer.send(request);
        return null;
    }

}