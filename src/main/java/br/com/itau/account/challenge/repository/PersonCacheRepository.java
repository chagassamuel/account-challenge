package br.com.itau.account.challenge.repository;

import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Component
public class PersonCacheRepository {

    private final RedisTemplate<String, PersonCacheEntity> redisTemplate;

    @CircuitBreaker(name = "redisSavePersonCB", fallbackMethod = "saveFallback")
    public void save(final PersonCacheEntity entity) {
        redisTemplate.opsForValue().set(entity.idPerson(), entity);
    }

    @CircuitBreaker(name = "redisFindPersonCB", fallbackMethod = "findFallback")
    public Optional<PersonCacheEntity> findById(final String id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(id));
    }

    private void saveFallback(final Throwable e) {
        log.warn("CB saveFallback was called", e);
    }

    private Optional<PersonCacheEntity> findFallback(final Throwable e) {
        log.warn("CB findFallback was called", e);
        return Optional.empty();
    }

}
