package br.com.itau.account.challenge.utils;

import br.com.itau.account.challenge.controller.domain.request.TransferAccountRequest;
import br.com.itau.account.challenge.controller.domain.response.BalanceAccountResponse;
import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.integration.bacen.domain.response.BacenResponse;
import br.com.itau.account.challenge.integration.hw.domain.response.HWResponse;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import br.com.itau.account.challenge.repository.entity.ErrorNotifyBacenEntity;
import br.com.itau.account.challenge.repository.entity.StatementEntity;
import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;

import java.util.List;

public class JsonHandlerMock {

    public static BacenRequest getBacenRequest() {
        return (BacenRequest) ResourceUtils.getObject("json/br/com/itau/account/challenge/integration/bacen/BacenRequest.json", BacenRequest.class);
    }

    public static BacenResponse getBacenResponse() {
        return (BacenResponse) ResourceUtils.getObject("json/br/com/itau/account/challenge/integration/bacen/BacenResponse.json", BacenResponse.class);
    }

    public static ErrorNotifyBacenEntity getErrorNotifyBacenEntity() {
        return (ErrorNotifyBacenEntity) ResourceUtils.getObject("json/br/com/itau/account/challenge/repository/entity/ErrorNotifyBacenEntity.json", ErrorNotifyBacenEntity.class);
    }

    public static HWResponse getHWResponse() {
        return (HWResponse) ResourceUtils.getObject("json/br/com/itau/account/challenge/integration/hw/HWResponse.json", HWResponse.class);
    }

    public static PersonCacheEntity getPersonCacheEntity() {
        return (PersonCacheEntity) ResourceUtils.getObject("json/br/com/itau/account/challenge/repository/entity/cache/PersonCacheEntity.json", PersonCacheEntity.class);
    }

    public static AccountEntity getAccountEntity() {
        return (AccountEntity) ResourceUtils.getObject("json/br/com/itau/account/challenge/repository/entity/AccountEntity.json", AccountEntity.class);
    }

    public static StatementEntity getDebitStatementEntity() {
        return (StatementEntity) ResourceUtils.getObject("json/br/com/itau/account/challenge/repository/entity/DebitStatementEntity.json", StatementEntity.class);
    }

    public static StatementEntity getCreditStatementEntity() {
        return (StatementEntity) ResourceUtils.getObject("json/br/com/itau/account/challenge/repository/entity/CreditStatementEntity.json", StatementEntity.class);
    }

    public static List<StatementEntity> getStatementsWithLimit() {
        return ResourceUtils.getListObject("json/br/com/itau/account/challenge/repository/entity/StatementEntityWithLimit.json", StatementEntity.class);
    }

    public static BalanceAccountResponse getBalanceAccountResponse() {
        return (BalanceAccountResponse) ResourceUtils.getObject("json/br/com/itau/account/challenge/controller/BalanceAccountResponse.json", BalanceAccountResponse.class);
    }

    public static TransferAccountRequest getTransferAccountRequest() {
        return (TransferAccountRequest) ResourceUtils.getObject("json/br/com/itau/account/challenge/controller/TransferAccountRequest.json", TransferAccountRequest.class);
    }

}
