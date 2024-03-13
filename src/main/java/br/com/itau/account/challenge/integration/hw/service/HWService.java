package br.com.itau.account.challenge.integration.hw.service;

import br.com.itau.account.challenge.integration.hw.client.HWClient;
import br.com.itau.account.challenge.integration.hw.domain.response.HWResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class HWService {

    private final HWClient hwClient;


    public HWResponse getPerson(final String idPerson) {
        final ResponseEntity<HWResponse> response = hwClient.getPerson(idPerson);
        if (isNull(response.getBody())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("person with id='%s' not found", idPerson));
        }
        return response.getBody();
    }

    @Retry(name = "personRT", fallbackMethod = "getPersonFullnameFallback")
    @CircuitBreaker(name = "personCB")
    @Cacheable("persons")
    public String getPersonFullname(final String idPerson) {
        return this.getPerson(idPerson).fullname();
    }

    private String getPersonFullnameFallback(final Throwable e) {
        return null;
    }

}