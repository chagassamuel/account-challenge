package br.com.itau.account.challenge.integration.bacen.service;

import br.com.itau.account.challenge.integration.bacen.client.BacenClient;
import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.integration.bacen.domain.response.BacenResponse;
import br.com.itau.account.challenge.mapper.BacenMapper;
import br.com.itau.account.challenge.repository.ErrorNotifyBacenRepository;
import br.com.itau.account.challenge.repository.entity.ErrorNotifyBacenEntity;
import br.com.itau.account.challenge.utils.JsonHandlerMock;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BacenServiceTest {

    @InjectMocks
    private BacenService bacenService;

    @Mock
    private BacenClient bacenClient;

    @Mock
    private BacenMapper bacenMapper;

    @Mock
    private ErrorNotifyBacenRepository errorNotifyBacenRepository;

    private BacenRequest bacenRequest;
    private BacenResponse bacenResponse;
    private ErrorNotifyBacenEntity errorNotifyBacenEntity;

    @BeforeEach
    public void setup() {
        bacenRequest = JsonHandlerMock.getBacenRequest();
        bacenResponse = JsonHandlerMock.getBacenResponse();
        errorNotifyBacenEntity = JsonHandlerMock.getErrorNotifyBacenEntity();
    }

    @Test
    public void notifyTransferTest() {
        when(bacenClient.notifyTransfer(any())).thenReturn(new ResponseEntity<>(bacenResponse, HttpStatus.CREATED));

        final BacenResponse response = bacenService.notifyTransfer(bacenRequest);

        assertEquals(bacenResponse.idBacen(), response.idBacen());
        assertEquals(bacenResponse.effectiveDateTime(), response.effectiveDateTime());

        verify(bacenClient, times(1)).notifyTransfer(any());
    }

    @Test
    public void notifyTransferFallbackTest() {
        when(bacenMapper.toErrorNotifyBacenEntity(bacenRequest)).thenReturn(errorNotifyBacenEntity);
        when(errorNotifyBacenRepository.save(any())).thenReturn(errorNotifyBacenEntity);

        final BacenResponse response = bacenService.notifyTransferFallback(bacenRequest, new Throwable());

        assertNull(response);

        verify(errorNotifyBacenRepository, times(1)).save(any());
    }

    @Test
    public void notifyTransferCircuitBreakerTest() {
        final CircuitBreakerConfig config = CircuitBreakerConfig.custom().failureRateThreshold(20).slidingWindowSize(5).build();
        final CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        final CircuitBreaker circuitBreaker = registry.circuitBreaker("bacenCB");
        final Function<BacenRequest, ResponseEntity<BacenResponse>> decorated = CircuitBreaker.decorateFunction(circuitBreaker, bacenClient::notifyTransfer);

        when(bacenClient.notifyTransfer(any())).thenThrow(new RuntimeException());

        IntStream.range(0, 10).forEach(i -> {
            try {
                decorated.apply(bacenRequest);
            } catch (Exception ignore) {
            }
        });


        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        verify(bacenClient, times(5)).notifyTransfer(any());
    }

    @Test
    public void notifyTransferRetryTest() {
        final RetryConfig config = RetryConfig.custom().maxAttempts(3).build();
        final RetryRegistry registry = RetryRegistry.of(config);
        final Retry retry = registry.retry("bacenRT");
        final Function<BacenRequest, Object> decorated = Retry.decorateFunction(retry, (final BacenRequest request) -> {
            bacenClient.notifyTransfer(request);
            return null;
        });

        when(bacenClient.notifyTransfer(any())).thenThrow(new RuntimeException());

        try {
            decorated.apply(bacenRequest);
        } catch (Exception e) {
            verify(bacenClient, times(3)).notifyTransfer(any());
        }
    }

}
