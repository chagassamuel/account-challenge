package br.com.itau.account.challenge.integration.bacen.client;

import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;
import br.com.itau.account.challenge.integration.bacen.domain.response.BacenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "bacenClient", url = "${bacen.url}")
public interface BacenClient {

    @PostMapping(value = "/bacen/transfer", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<BacenResponse> notifyTransfer(final BacenRequest request);

}