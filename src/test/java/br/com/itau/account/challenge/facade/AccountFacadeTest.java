package br.com.itau.account.challenge.facade;

import br.com.itau.account.challenge.controller.domain.request.TransferAccountRequest;
import br.com.itau.account.challenge.controller.domain.response.BalanceAccountResponse;
import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.integration.bacen.domain.response.BacenResponse;
import br.com.itau.account.challenge.integration.bacen.service.BacenService;
import br.com.itau.account.challenge.integration.hw.domain.response.HWResponse;
import br.com.itau.account.challenge.integration.hw.service.HWService;
import br.com.itau.account.challenge.mapper.AccountMapper;
import br.com.itau.account.challenge.mapper.BacenMapper;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import br.com.itau.account.challenge.repository.entity.StatementEntity;
import br.com.itau.account.challenge.service.AccountService;
import br.com.itau.account.challenge.utils.JsonHandlerMock;
import br.com.itau.account.challenge.utils.RxJavaUtil;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountFacadeTest {

    @InjectMocks
    private AccountFacade accountFacade;

    @Mock
    private AccountService accountService;

    @Mock
    private HWService hwService;

    @Mock
    private BacenService bacenService;

    @Mock
    private RxJavaUtil rxJavaUtil;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private BacenMapper bacenMapper;

    private AccountEntity accountEntity;
    private TransferAccountRequest transferAccountRequest;

    @BeforeEach
    public void setup() {
        accountEntity = JsonHandlerMock.getAccountEntity();
        transferAccountRequest = JsonHandlerMock.getTransferAccountRequest();
    }

    @Test
    public void getBalanceTest() {
        final BalanceAccountResponse balanceAccountResponse = JsonHandlerMock.getBalanceAccountResponse();

        when(accountService.getAccount(anyString())).thenReturn(accountEntity);
        when(accountMapper.toBalanceAccountResponse(any())).thenReturn(balanceAccountResponse);

        final BalanceAccountResponse response = accountFacade.getBalance(accountEntity.getIdAccount());

        assertNotNull(response);
        assertEquals(balanceAccountResponse.idAccount(), response.idAccount());
        assertEquals(balanceAccountResponse.balance(), response.balance());
        assertEquals(balanceAccountResponse.status(), response.status());

        verify(accountService, times(1)).getAccount(any());
        verify(accountMapper, times(1)).toBalanceAccountResponse(any());
    }

    @Test
    public void transferTest() {
        final StatementEntity statementEntity = JsonHandlerMock.getDebitStatementEntity();
        final HWResponse hwResponse = JsonHandlerMock.getHWResponse();
        final BacenRequest bacenRequest = JsonHandlerMock.getBacenRequest();
        final BacenResponse bacenResponse = JsonHandlerMock.getBacenResponse();

        when(accountService.getActiveAccount(anyString())).thenReturn(accountEntity);
        when(accountService.haveSufficientBalance(any(), any())).thenReturn(true);
        when(accountService.haveDailyTransferLimit(anyString(), any())).thenReturn(true);
        when(accountMapper.toStatementEntity(any(), any(), any())).thenReturn(statementEntity);
        when(hwService.getPersonFullname(any())).thenReturn(hwResponse.fullname());
        when(rxJavaUtil.getObservableParallel()).thenReturn(Observable.just(1).observeOn(Schedulers.computation()));
        when(bacenMapper.toBacenRequest(any(), any(), any(), any(), any())).thenReturn(bacenRequest);
        when(bacenService.notifyTransfer(any())).thenReturn(bacenResponse);

        accountFacade.transfer(transferAccountRequest);

        verify(accountService, times(2)).getActiveAccount(any());
        verify(accountService, times(1)).haveSufficientBalance(any(), any());
        verify(accountService, times(1)).haveDailyTransferLimit(anyString(), any());
        verify(accountMapper, times(2)).toStatementEntity(any(), any(), any());
        verify(hwService, times(2)).getPersonFullname(any());
        verify(rxJavaUtil, times(2)).getObservableParallel();
        verify(bacenMapper, times(1)).toBacenRequest(any(), any(), any(), any(), any());
        verify(bacenService, times(1)).notifyTransfer(any());
    }

    @Test
    public void transferWithoutSufficienteBalanceTest() {
        when(accountService.getActiveAccount(anyString())).thenReturn(accountEntity);
        when(accountService.haveSufficientBalance(any(), any())).thenReturn(false);

        accountFacade.transfer(transferAccountRequest);

        verify(accountService, times(1)).getActiveAccount(any());
        verify(accountService, times(1)).haveSufficientBalance(any(), any());
        verify(accountService, times(0)).haveDailyTransferLimit(anyString(), any());
        verify(accountMapper, times(0)).toStatementEntity(any(), any(), any());
        verify(hwService, times(0)).getPersonFullname(any());
        verify(rxJavaUtil, times(0)).getObservableParallel();
        verify(bacenMapper, times(0)).toBacenRequest(any(), any(), any(), any(), any());
        verify(bacenService, times(0)).notifyTransfer(any());
    }

    @Test
    public void transferWithoutDailyTransferLimitTest() {
        when(accountService.getActiveAccount(anyString())).thenReturn(accountEntity);
        when(accountService.haveSufficientBalance(any(), any())).thenReturn(true);
        when(accountService.haveDailyTransferLimit(anyString(), any())).thenReturn(false);

        accountFacade.transfer(transferAccountRequest);

        verify(accountService, times(1)).getActiveAccount(any());
        verify(accountService, times(1)).haveSufficientBalance(any(), any());
        verify(accountService, times(1)).haveDailyTransferLimit(anyString(), any());
        verify(accountMapper, times(0)).toStatementEntity(any(), any(), any());
        verify(hwService, times(0)).getPersonFullname(any());
        verify(rxJavaUtil, times(0)).getObservableParallel();
        verify(bacenMapper, times(0)).toBacenRequest(any(), any(), any(), any(), any());
        verify(bacenService, times(0)).notifyTransfer(any());
    }

}