package br.com.itau.account.challenge.repository;

import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class PersonCacheRepository {

    private final RedisTemplate<String, PersonCacheEntity> redisTemplate;

    public void save(final PersonCacheEntity entity) {
        redisTemplate.opsForValue().set(entity.idPerson(), entity);
    }

    public Optional<PersonCacheEntity> findById(final String id) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(id));
    }

}
