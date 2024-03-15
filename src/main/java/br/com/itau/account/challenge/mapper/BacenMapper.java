package br.com.itau.account.challenge.mapper;

import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import br.com.itau.account.challenge.repository.entity.ErrorNotifyBacenEntity;
import br.com.itau.account.challenge.utils.JsonUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;

@Mapper(componentModel = "spring", imports = {JsonUtil.class})
public interface BacenMapper {

    BacenRequest toBacenRequest(final AccountEntity originAccount,
                                final String originFullname,
                                final AccountEntity targetAccount,
                                final String targetFullname,
                                final BigDecimal value);

    @Mapping(target = "payload", expression = "java(JsonUtil.objectToJson(bacenRequest))")
    ErrorNotifyBacenEntity toErrorNotifyBacenEntity(final BacenRequest bacenRequest);

}