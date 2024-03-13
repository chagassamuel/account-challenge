package br.com.itau.account.challenge.config;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableCaching
@EnableScheduling
@Configuration
public class CacheConfig {

    @CacheEvict(allEntries = true, cacheNames = {"persons"})
    @Scheduled(cron = "${cache.evict.cron}")
    public void cacheEvict() {

    }

}
