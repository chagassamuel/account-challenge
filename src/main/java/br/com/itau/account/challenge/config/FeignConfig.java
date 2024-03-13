package br.com.itau.account.challenge.config;

import feign.RetryableException;
import feign.Retryer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class FeignConfig {

    @Bean
    @Primary
    public Retryer myCustomRetryer() {
        return new CustomRetryer();
    }

    public class CustomRetryer implements Retryer {

        private final int maxAttempts;
        private final long backoff;
        private int attempt;

        public CustomRetryer() {
            this(5, 3);
        }

        public CustomRetryer(final long backoff, final int maxAttempts) {
            this.backoff = backoff;
            this.maxAttempts = maxAttempts;
            attempt = 1;
        }

        @Override
        public void continueOrPropagate(RetryableException e) {
            log.debug("ATTEMPT RETRY: {} - {}:{}", attempt, e.method(), e.request().requestTemplate().url());
            if (attempt++ >= maxAttempts) {
                throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "service unavailable");
            }
            try {
                Thread.sleep(backoff);
            } catch (final Exception ignored) {
                Thread.currentThread().interrupt();
            }
        }

        @Override
        public Retryer clone() {
            return new CustomRetryer();
        }
    }

}