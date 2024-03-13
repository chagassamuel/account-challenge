package br.com.itau.account.challenge.repository;

import br.com.itau.account.challenge.repository.domain.AccountStatusEnum;
import br.com.itau.account.challenge.repository.entity.AccountEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends CrudRepository<AccountEntity, String> {

    Optional<AccountEntity> findByIdAccountAndStatus(final String idAccount, final AccountStatusEnum status);

}