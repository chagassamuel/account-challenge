package br.com.itau.account.challenge;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@EnableFeignClients(basePackages = {"br.com.itau.account.challenge.integration"})
@SpringBootApplication
public class AccountChallengeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountChallengeApplication.class, args);
        log.info("ACCOUNT-CHALLENGE STARTED");
    }

}
