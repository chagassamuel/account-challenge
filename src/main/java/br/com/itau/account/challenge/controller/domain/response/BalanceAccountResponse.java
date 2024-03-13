package br.com.itau.account.challenge.controller.domain.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record BalanceAccountResponse(String idAccount, String balance, String status) {
}