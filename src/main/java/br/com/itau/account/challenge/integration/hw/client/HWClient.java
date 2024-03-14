package br.com.itau.account.challenge.integration.hw.client;

import br.com.itau.account.challenge.integration.hw.domain.response.HWResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "hwClient")
public interface HWClient {

    @GetMapping(value = "/pessoas_fisicas/{id_pessoa}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<HWResponse> getPerson(@PathVariable(name = "id_pessoa") final String idPerson);

}