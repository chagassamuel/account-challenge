package br.com.itau.account.challenge.mapper;

import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BacenMapper {

    BacenRequest toBacenRequest(final AccountEntity originAccount,
                                final String originFullname,
                                final AccountEntity targetAccount,
                                final String targetFullname,
                                final BigDecimal value);

}