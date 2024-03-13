package br.com.itau.account.challenge.mapper;

import br.com.itau.account.challenge.controller.domain.request.TransferAccountRequest;
import br.com.itau.account.challenge.controller.domain.response.BalanceAccountResponse;
import br.com.itau.account.challenge.repository.domain.StatementTypeEnum;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import br.com.itau.account.challenge.repository.entity.StatementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = {StatementTypeEnum.class})
public interface AccountMapper {

    BalanceAccountResponse toBalanceAccountResponse(final AccountEntity accountEntity);

    @Mapping(target = "idAccountInvolved", expression = "java(StatementTypeEnum.DEBIT.equals(type)? request.idTargetAccount() : request.idOriginAccount())")
    StatementEntity toStatementEntity(final AccountEntity account, final TransferAccountRequest request, final StatementTypeEnum type);

}