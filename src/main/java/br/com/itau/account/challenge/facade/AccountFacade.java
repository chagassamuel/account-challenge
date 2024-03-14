package br.com.itau.account.challenge.facade;

import br.com.itau.account.challenge.controller.domain.request.TransferAccountRequest;
import br.com.itau.account.challenge.controller.domain.response.BalanceAccountResponse;
import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.integration.bacen.service.BacenService;
import br.com.itau.account.challenge.integration.hw.service.HWService;
import br.com.itau.account.challenge.mapper.AccountMapper;
import br.com.itau.account.challenge.mapper.BacenMapper;
import br.com.itau.account.challenge.repository.domain.StatementTypeEnum;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import br.com.itau.account.challenge.repository.entity.StatementEntity;
import br.com.itau.account.challenge.service.AccountService;
import br.com.itau.account.challenge.utils.RxJavaUtil;
import io.reactivex.rxjava3.core.Observable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AccountFacade {

    private final AccountService accountService;
    private final HWService hwService;
    private final BacenService bacenService;
    private final RxJavaUtil rxJavaUtil;

    private final AccountMapper accountMapper;
    private final BacenMapper bacenMapper;

    public BalanceAccountResponse getBalance(final String idAccount) {
        final AccountEntity accountEntity = accountService.getAccount(idAccount);

        // other business rules

        return accountMapper.toBalanceAccountResponse(accountEntity);
    }

    public void transfer(final TransferAccountRequest transferAccountRequest) {
        final AccountEntity originAccountEntity = accountService.getActiveAccount(transferAccountRequest.idOriginAccount());
        if (accountService.haveSufficientBalance(originAccountEntity, transferAccountRequest.value())
                && accountService.haveDailyTransferLimit(transferAccountRequest.idOriginAccount(), transferAccountRequest.value())) {

            final AccountEntity targetAccountEntity = accountService.getActiveAccount(transferAccountRequest.idTargetAccount());
            final StatementEntity originDebit = accountMapper.toStatementEntity(originAccountEntity, transferAccountRequest, StatementTypeEnum.DEBIT);
            final StatementEntity targetCredit = accountMapper.toStatementEntity(targetAccountEntity, transferAccountRequest, StatementTypeEnum.CREDIT);
            accountService.transfer(originAccountEntity, targetAccountEntity, originDebit, targetCredit, transferAccountRequest.value());

            final Observable<String> originFullnameObs = rxJavaUtil.getObservableParallel().map(s -> hwService.getPersonFullname(originAccountEntity.getIdPerson()));
            final Observable<String> targetFullnameObs = rxJavaUtil.getObservableParallel().map(s -> hwService.getPersonFullname(targetAccountEntity.getIdPerson()));

            final BacenRequest bacenRequest = Observable.zip(originFullnameObs, targetFullnameObs, (originFullname, targetFullname) ->
                    bacenMapper.toBacenRequest(originAccountEntity, originFullname,targetAccountEntity, targetFullname, transferAccountRequest.value())
            ).blockingFirst();

            bacenService.notifyTransfer(bacenRequest);
        }
    }

}
