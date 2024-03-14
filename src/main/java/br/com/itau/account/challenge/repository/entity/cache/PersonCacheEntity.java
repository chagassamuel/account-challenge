package br.com.itau.account.challenge.repository.entity.cache;

import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "tb_itau_person_cache", timeToLive = 60 * 60 * 24)
public record PersonCacheEntity(String idPerson, String fullname) {

}
