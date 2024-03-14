package br.com.itau.account.challenge.integration.hw.service;

import br.com.itau.account.challenge.integration.hw.client.HWClient;
import br.com.itau.account.challenge.integration.hw.domain.response.HWResponse;
import br.com.itau.account.challenge.mapper.HWMapper;
import br.com.itau.account.challenge.repository.PersonCacheRepository;
import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
@Service
public class HWService {

    private final HWClient hwClient;
    private final HWMapper hwMapper;
    private final PersonCacheRepository personCacheRepository;

    @Retry(name = "personRT", fallbackMethod = "getPersonFullnameFallback")
    @CircuitBreaker(name = "personCB")
    public String getPersonFullname(final String idPerson) {
        return this.getPerson(idPerson).fullname();
    }

    public HWResponse getPerson(final String idPerson) {
        final Optional<PersonCacheEntity> cacheEntity = personCacheRepository.findById(idPerson);
        if (cacheEntity.isPresent()) {
            return hwMapper.toHWResponse(cacheEntity.get());
        }

        final HWResponse hwResponse = getPersonHW(idPerson);
        personCacheRepository.save(hwMapper.toPersonCacheEntity(hwResponse));

        return hwResponse;
    }

    private HWResponse getPersonHW(final String idPerson) {
        System.out.println("buscando: " + idPerson);
        final ResponseEntity<HWResponse> hwResponse = hwClient.getPerson(idPerson);
        if (isNull(hwResponse.getBody())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("person with id='%s' not found", idPerson));
        }
        return hwResponse.getBody();
    }

    private String getPersonFullnameFallback(final Throwable e) {
        return null;
    }

}