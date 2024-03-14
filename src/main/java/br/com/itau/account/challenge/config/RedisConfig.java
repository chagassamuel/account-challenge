package br.com.itau.account.challenge.config;

import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;


@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisConfig {

    @Bean
    public RedisTemplate<String, PersonCacheEntity> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, PersonCacheEntity> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

}