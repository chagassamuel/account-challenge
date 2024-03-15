package br.com.itau.account.challenge.utils;

import br.com.itau.account.challenge.integration.bacen.domain.request.BacenRequest;

public class JsonHandlerMock {

    public static BacenRequest getBacenRequest() {
        return (BacenRequest) ResourceUtils.getObject("json/br/com/itau/account/challenge/integration/bacen/service/BacenRequest.json", BacenRequest.class);
    }

}
