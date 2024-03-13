package br.com.itau.account.challenge.controller.domain;

import br.com.itau.account.challenge.controller.domain.request.TransferAccountRequest;
import br.com.itau.account.challenge.controller.domain.response.BalanceAccountResponse;
import br.com.itau.account.challenge.facade.AccountFacade;
import feign.Response;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/v1")
@RestController
public class AccountController {

    private final AccountFacade accountFacade;

    @GetMapping("/account/balance/{id_account}")
    public ResponseEntity<BalanceAccountResponse> getBalance(@PathVariable(name = "id_account") @NotEmpty final String idAccount) {
        return ResponseEntity.ok(accountFacade.getBalance(idAccount));
    }

    @PostMapping("/account/transfer")
    public ResponseEntity<Void> transfer(@Valid @RequestBody final TransferAccountRequest request) {
        accountFacade.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}