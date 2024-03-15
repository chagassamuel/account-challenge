package br.com.itau.account.challenge.integration.bacen.service;

import br.com.itau.account.challenge.integration.bacen.client.BacenClient;
import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.integration.bacen.domain.response.BacenResponse;
import br.com.itau.account.challenge.integration.bacen.service.BacenService;
import br.com.itau.account.challenge.utils.JsonHandlerMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BacenServiceTest {

    @InjectMocks
    private BacenService bacenService;

    @Mock
    private BacenClient bacenClient;

    @Test
    public void notifyTransferTest() {
        final BacenRequest bacenRequest = JsonHandlerMock.getBacenRequest();
        final BacenResponse bacenResponse = new BacenResponse("123", LocalDateTime.now());
        when(bacenClient.notifyTransfer(any())).thenReturn(new ResponseEntity<>(bacenResponse, HttpStatus.CREATED));
        final BacenResponse response = bacenService.notifyTransfer(null);
        Assertions.assertEquals("123", response.idBacen());
    }

}
