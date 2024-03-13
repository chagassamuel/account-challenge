package br.com.itau.account.challenge.repository;

import br.com.itau.account.challenge.repository.entity.StatementEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatementRepository extends CrudRepository<StatementEntity, String> {

    List<StatementEntity> findByAccountIdAccountAndEffectiveDate(final String idAccount, final LocalDate date);

}