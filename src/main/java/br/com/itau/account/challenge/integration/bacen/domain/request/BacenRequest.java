package br.com.itau.account.challenge.integration.bacen.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record BacenRequest(AccountRequest originAccount,
                           String originFullname,
                           AccountRequest targetAccount,
                           String targetFullname,
                           BigDecimal value) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public record AccountRequest(String companyCode,
                                 String agencyNumber,
                                 String accountNumber,
                                 String checkDigit) {

    }

}
