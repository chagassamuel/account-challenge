package br.com.itau.account.challenge.service;

import br.com.itau.account.challenge.repository.AccountRepository;
import br.com.itau.account.challenge.repository.StatementRepository;
import br.com.itau.account.challenge.repository.domain.AccountStatusEnum;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import br.com.itau.account.challenge.repository.entity.StatementEntity;
import br.com.itau.account.challenge.utils.JsonHandlerMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {


    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private StatementRepository statementRepository;

    private final String idAccount = UUID.randomUUID().toString();
    private AccountEntity accountEntity;

    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(accountService, "DAILY_TRANSFER_LIMIT", BigDecimal.valueOf(1000.00));
        accountEntity = JsonHandlerMock.getAccountEntity();
    }

    @Test
    public void getAccountTest() {
        when(accountRepository.findById(anyString())).thenReturn(Optional.ofNullable(accountEntity));

        final AccountEntity entity = accountService.getAccount(idAccount);

        assertNotNull(entity);
        assertEquals(accountEntity.getIdAccount(), entity.getIdAccount());
        assertEquals(accountEntity.getStatus(), entity.getStatus());

        verify(accountRepository, times(1)).findById(anyString());
    }

    @Test
    public void getAccountNotFoundTest() {
        when(accountRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> accountService.getAccount(idAccount));

        verify(accountRepository, times(1)).findById(anyString());
        verify(accountRepository, times(0)).findByIdAccountAndStatus(anyString(), any());
    }

    @Test
    public void getActiveAccountTest() {
        when(accountRepository.findByIdAccountAndStatus(anyString(), any())).thenReturn(Optional.ofNullable(accountEntity));

        final AccountEntity entity = accountService.getActiveAccount(idAccount);

        assertNotNull(entity);
        assertEquals(accountEntity.getIdAccount(), entity.getIdAccount());
        assertEquals(AccountStatusEnum.ACTIVE, entity.getStatus());

        verify(accountRepository, times(0)).findById(anyString());
        verify(accountRepository, times(1)).findByIdAccountAndStatus(anyString(), any());
    }

    @Test
    public void getActiveAccountNotFoundTest() {
        when(accountRepository.findByIdAccountAndStatus(anyString(), any())).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> accountService.getActiveAccount(idAccount));

        verify(accountRepository, times(0)).findById(anyString());
        verify(accountRepository, times(1)).findByIdAccountAndStatus(anyString(), any());
    }

    @ParameterizedTest
    @MethodSource("argumentsHaveSufficientBalanceTest")
    public void haveSufficientBalanceTest(final BigDecimal value) {
        assertTrue(accountService.haveSufficientBalance(accountEntity.getBalance(), value));
    }

    @ParameterizedTest
    @MethodSource("argumentsHaveInsufficientBalanceTest")
    public void haveInsufficientBalanceTest(final BigDecimal value) {
        assertThrows(ResponseStatusException.class, () -> accountService.haveSufficientBalance(accountEntity.getBalance(), value));
    }

    @ParameterizedTest
    @MethodSource("argumentsHaveDailyTransferLimitTest")
    public void haveDailyTransferLimitTest(final BigDecimal value) {
        final List<StatementEntity> statements = JsonHandlerMock.getStatementsWithLimit();

        when(statementRepository.findByAccountIdAccountAndEffectiveDate(anyString(), any())).thenReturn(statements);

        assertTrue(accountService.haveDailyTransferLimit(accountEntity.getIdAccount(), value));

        verify(statementRepository, times(1)).findByAccountIdAccountAndEffectiveDate(anyString(), any());
    }

    @ParameterizedTest
    @MethodSource("argumentsNotHaveDailyTransferLimitTest")
    public void notHaveDailyTransferLimitTest(final BigDecimal value) {
        final List<StatementEntity> statements = JsonHandlerMock.getStatementsWithLimit();

        when(statementRepository.findByAccountIdAccountAndEffectiveDate(anyString(), any())).thenReturn(statements);

        assertThrows(ResponseStatusException.class, () -> accountService.haveDailyTransferLimit(accountEntity.getIdAccount(), value));

        verify(statementRepository, times(1)).findByAccountIdAccountAndEffectiveDate(anyString(), any());
    }

    @Test
    public void transferTest() {
        final AccountEntity targetAccountEntity = JsonHandlerMock.getAccountEntity();
        final StatementEntity debitStatementEntity = JsonHandlerMock.getDebitStatementEntity();
        final StatementEntity creditStatementEntity = JsonHandlerMock.getCreditStatementEntity();
        final BigDecimal balanceOriginAccountBeforeTransfer = accountEntity.getBalance();
        final BigDecimal balanceTargetAccountBeforeTransfer = targetAccountEntity.getBalance();
        final BigDecimal value = BigDecimal.valueOf(1000.00);

        accountService.transfer(accountEntity, targetAccountEntity, debitStatementEntity, creditStatementEntity, value);

        assertEquals(balanceOriginAccountBeforeTransfer, accountEntity.getBalance().add(value));
        assertEquals(balanceTargetAccountBeforeTransfer, targetAccountEntity.getBalance().subtract(value));
    }

    private static Stream<Arguments> argumentsHaveSufficientBalanceTest() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(100.00)),
                Arguments.of(BigDecimal.valueOf(1000.00)),
                Arguments.of(BigDecimal.valueOf(10000.00)));
    }

    private static Stream<Arguments> argumentsHaveInsufficientBalanceTest() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(10000.01)),
                Arguments.of(BigDecimal.valueOf(10001.00)),
                Arguments.of(BigDecimal.valueOf(100000.00)));
    }

    private static Stream<Arguments> argumentsHaveDailyTransferLimitTest() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(10.00)),
                Arguments.of(BigDecimal.valueOf(30.00)),
                Arguments.of(BigDecimal.valueOf(50.00)));
    }

    private static Stream<Arguments> argumentsNotHaveDailyTransferLimitTest() {
        return Stream.of(
                Arguments.of(BigDecimal.valueOf(50.01)),
                Arguments.of(BigDecimal.valueOf(51.00)),
                Arguments.of(BigDecimal.valueOf(100.00)));
    }

}
