package br.com.itau.account.challenge.controller.domain.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;

import java.math.BigDecimal;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public record TransferAccountRequest(@NotEmpty String idOriginAccount,
                                     @NotEmpty String idTargetAccount,
                                     @DecimalMin(value = "0.01", message = "the value must be positive") BigDecimal value) {
}