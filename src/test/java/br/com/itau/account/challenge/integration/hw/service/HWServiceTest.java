package br.com.itau.account.challenge.integration.hw.service;


import br.com.itau.account.challenge.integration.hw.client.HWClient;
import br.com.itau.account.challenge.integration.hw.domain.response.HWResponse;
import br.com.itau.account.challenge.mapper.HWMapper;
import br.com.itau.account.challenge.repository.PersonCacheRepository;
import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;
import br.com.itau.account.challenge.utils.JsonHandlerMock;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HWServiceTest {

    @InjectMocks
    private HWService hwService;

    @Mock
    private HWClient hwClient;

    @Mock
    private HWMapper hwMapper;

    @Mock
    private PersonCacheRepository personCacheRepository;

    private final String idPerson = UUID.randomUUID().toString();
    private HWResponse hwResponse;
    private PersonCacheEntity personCacheEntity;

    @BeforeEach
    public void setup() {
        hwResponse = JsonHandlerMock.getHWResponse();
        personCacheEntity = JsonHandlerMock.getPersonCacheEntity();
    }

    @Test
    public void getPersonFullnameTest() {
        when(personCacheRepository.findById(anyString())).thenReturn(Optional.empty());
        when(hwClient.getPerson(any())).thenReturn(new ResponseEntity<>(hwResponse, HttpStatus.OK));
        when(hwMapper.toPersonCacheEntity(any())).thenReturn(personCacheEntity);
        doNothing().when(personCacheRepository).save(any());

        final String response = hwService.getPersonFullname(idPerson);

        assertEquals(hwResponse.fullname(), response);

        verify(personCacheRepository, times(1)).findById(anyString());
        verify(hwMapper, times(0)).toHWResponse(any());
        verify(hwClient, times(1)).getPerson(any());
        verify(hwMapper, times(1)).toPersonCacheEntity(any());
        verify(personCacheRepository, times(1)).save(any());
    }

    @Test
    public void getPersonFullnameCachedTest() {
        when(personCacheRepository.findById(anyString())).thenReturn(Optional.ofNullable(personCacheEntity));
        when(hwMapper.toHWResponse(any())).thenReturn(hwResponse);

        final String response = hwService.getPersonFullname(idPerson);

        assertEquals(hwResponse.fullname(), response);

        verify(personCacheRepository, times(1)).findById(anyString());
        verify(hwMapper, times(1)).toHWResponse(any());
        verify(hwClient, times(0)).getPerson(any());
        verify(hwMapper, times(0)).toPersonCacheEntity(any());
        verify(personCacheRepository, times(0)).save(any());
    }

    @Test
    public void getPersonFullnameNotFoundTest() {
        when(personCacheRepository.findById(anyString())).thenReturn(Optional.empty());
        when(hwClient.getPerson(any())).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

        final String response = hwService.getPersonFullname(idPerson);

        assertEquals(StringUtils.EMPTY, response);

        verify(personCacheRepository, times(1)).findById(anyString());
        verify(hwMapper, times(0)).toHWResponse(any());
        verify(hwClient, times(1)).getPerson(any());
        verify(hwMapper, times(0)).toPersonCacheEntity(any());
        verify(personCacheRepository, times(0)).save(any());
    }

    @Test
    public void getPersonFullnameFallbackTest() {
        final String response = hwService.getPersonFullnameFallback(new Throwable());

        assertEquals(StringUtils.EMPTY, response);
    }

    @Test
    public void getPersonFullnameCircuitBreakerTest() {
        final CircuitBreakerConfig config = CircuitBreakerConfig.custom().failureRateThreshold(20).slidingWindowSize(5).build();
        final CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        final CircuitBreaker circuitBreaker = registry.circuitBreaker("personCB");
        final Function<String, ResponseEntity<HWResponse>> decorated = CircuitBreaker.decorateFunction(circuitBreaker, hwClient::getPerson);

        when(hwClient.getPerson(any())).thenThrow(new RuntimeException());

        IntStream.range(0, 10).forEach(i -> {
            try {
                decorated.apply(idPerson);
            } catch (Exception ignore) {
            }
        });


        assertEquals(CircuitBreaker.State.OPEN, circuitBreaker.getState());

        verify(hwClient, times(5)).getPerson(any());
    }

    @Test
    public void getPersonFullnameRetryTest() {
        final RetryConfig config = RetryConfig.custom().maxAttempts(3).build();
        final RetryRegistry registry = RetryRegistry.of(config);
        final Retry retry = registry.retry("personRT");
        final Function<String, Object> decorated = Retry.decorateFunction(retry, (final String id) -> {
            hwClient.getPerson(id);
            return null;
        });

        when(hwClient.getPerson(any())).thenThrow(new RuntimeException());

        try {
            decorated.apply(idPerson);
        } catch (Exception e) {
            verify(hwClient, times(3)).getPerson(any());
        }
    }

}