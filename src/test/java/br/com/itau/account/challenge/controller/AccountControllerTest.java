package br.com.itau.account.challenge.controller;

import br.com.itau.account.challenge.controller.domain.request.TransferAccountRequest;
import br.com.itau.account.challenge.controller.domain.response.BalanceAccountResponse;
import br.com.itau.account.challenge.facade.AccountFacade;
import br.com.itau.account.challenge.utils.JsonHandlerMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountFacade accountFacade;

    private final String idAccount = "438cd6e9-538f-46d7-a97f-124543abf4eb";
    private BalanceAccountResponse balanceAccountResponse;

    @BeforeEach
    public void setup() {
        balanceAccountResponse = JsonHandlerMock.getBalanceAccountResponse();
    }

    @Test
    public void getBalanceTest() {
        when(accountFacade.getBalance(anyString())).thenReturn(balanceAccountResponse);

        final ResponseEntity<BalanceAccountResponse> response = accountController.getBalance(idAccount);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(balanceAccountResponse.idAccount(), response.getBody().idAccount());
        assertEquals(balanceAccountResponse.balance(), response.getBody().balance());
        assertEquals(balanceAccountResponse.status(), response.getBody().status());

        verify(accountFacade, times(1)).getBalance(anyString());
    }

    @Test
    public void transferTest() {
        final TransferAccountRequest transferAccountRequest = JsonHandlerMock.getTransferAccountRequest();
        final ResponseEntity<Void> response = accountController.transfer(transferAccountRequest);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());

        verify(accountFacade, times(1)).transfer(any());
    }

}