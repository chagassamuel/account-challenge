package br.com.itau.account.challenge.integration.hw.service;

import br.com.itau.account.challenge.integration.hw.client.HWClient;
import br.com.itau.account.challenge.integration.hw.domain.response.HWResponse;
import br.com.itau.account.challenge.mapper.HWMapper;
import br.com.itau.account.challenge.repository.PersonCacheRepository;
import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@RequiredArgsConstructor
@Service
public class HWService {

    private final HWClient hwClient;
    private final HWMapper hwMapper;
    private final PersonCacheRepository personCacheRepository;

    @Retry(name = "personRT", fallbackMethod = "getPersonFullnameFallback")
    @CircuitBreaker(name = "personCB")
    public String getPersonFullname(final String idPerson) {
        final HWResponse hwResponse = this.getPersonWithCache(idPerson);
        return isNull(hwResponse) ? StringUtils.EMPTY : hwResponse.fullname();
    }

    public HWResponse getPersonWithCache(final String idPerson) {
        final Optional<PersonCacheEntity> cacheEntity = personCacheRepository.findById(idPerson);
        if (cacheEntity.isPresent()) {
            return hwMapper.toHWResponse(cacheEntity.get());
        }

        final HWResponse hwResponse = this.getPerson(idPerson);
        if (isNull(hwResponse)) {
            return null;
        }

        personCacheRepository.save(hwMapper.toPersonCacheEntity(hwResponse));

        return hwResponse;
    }

    public HWResponse getPerson(final String idPerson) {
        return hwClient.getPerson(idPerson).getBody();
    }

    protected String getPersonFullnameFallback(final Throwable e) {
        log.warn("Retry getPersonFullnameFallback was called: {}", e.getMessage());
        return StringUtils.EMPTY;
    }

}