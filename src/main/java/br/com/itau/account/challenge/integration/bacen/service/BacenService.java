package br.com.itau.account.challenge.integration.bacen.service;

import br.com.itau.account.challenge.integration.bacen.client.BacenClient;
import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.integration.bacen.domain.response.BacenResponse;
import br.com.itau.account.challenge.mapper.BacenMapper;
import br.com.itau.account.challenge.repository.ErrorNotifyBacenRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class BacenService {

    private final BacenClient bacenClient;
    private final BacenMapper bacenMapper;
    private final ErrorNotifyBacenRepository errorNotifyBacenRepository;

    @Retry(name = "bacenRT", fallbackMethod = "notifyTransferFallback")
    @CircuitBreaker(name = "bacenCB")
    public BacenResponse notifyTransfer(final BacenRequest request) {
        return bacenClient.notifyTransfer(request).getBody();
    }

    protected BacenResponse notifyTransferFallback(final BacenRequest request, final Throwable e) {
        log.warn("Retry notifyTransferFallback was called", e);
        errorNotifyBacenRepository.save(bacenMapper.toErrorNotifyBacenEntity(request));
        return null;
    }

}