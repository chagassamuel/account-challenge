package br.com.itau.account.challenge.service;

import br.com.itau.account.challenge.repository.AccountRepository;
import br.com.itau.account.challenge.repository.StatementRepository;
import br.com.itau.account.challenge.repository.domain.AccountStatusEnum;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import br.com.itau.account.challenge.repository.entity.StatementEntity;
import br.com.itau.account.challenge.utils.BigDecimalUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AccountService {

    @Value("${daily.transfer.limit.value}")
    private BigDecimal DAILY_TRANSFER_LIMIT;

    private final AccountRepository accountRepository;
    private final StatementRepository statementRepository;

    public AccountEntity getAccount(final String idAccount) {
        final Optional<AccountEntity> accountEntity = accountRepository.findById(idAccount);
        return validateAccountEntity(accountEntity, idAccount);
    }

    public AccountEntity getActiveAccount(final String idAccount) {
        final Optional<AccountEntity> accountEntity = accountRepository.findByIdAccountAndStatus(idAccount, AccountStatusEnum.ACTIVE);
        return validateAccountEntity(accountEntity, idAccount);
    }

    public boolean haveSufficientBalance(final BigDecimal balance, final BigDecimal value) {
        if (BigDecimalUtil.greaterThan(value, balance)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "insufficient balance");
        }
        return true;
    }

    public boolean haveDailyTransferLimit(final String idAccount, final BigDecimal value) {
        final BigDecimal statementsToday = this.getStatementsToday(idAccount).stream().map(item -> item.getValue()).reduce(value, BigDecimal::add);
        if (BigDecimalUtil.greaterThan(statementsToday, DAILY_TRANSFER_LIMIT)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "insufficient daily transfer limit");
        }
        return true;
    }

    public List<StatementEntity> getStatementsToday(final String idAccount) {
        return statementRepository.findByAccountIdAccountAndEffectiveDate(idAccount, LocalDate.now());
    }

    public void transfer(final AccountEntity originAccountEntity, final AccountEntity targetAccountEntity,
                         final StatementEntity originDebit, final StatementEntity targetCredit, final BigDecimal value) {
        originAccountEntity.subtractBalance(value);
        targetAccountEntity.addBalance(value);

        statementRepository.saveAll(List.of(originDebit, targetCredit));
    }

    private AccountEntity validateAccountEntity(final Optional<AccountEntity> accountEntity, final String idAccount) {
        if (accountEntity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("account with id='%s' not available", idAccount));
        }
        return accountEntity.get();
    }

}