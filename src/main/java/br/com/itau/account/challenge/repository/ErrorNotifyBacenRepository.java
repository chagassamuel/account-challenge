package br.com.itau.account.challenge.repository;

import br.com.itau.account.challenge.repository.entity.ErrorNotifyBacenEntity;
import br.com.itau.account.challenge.repository.entity.StatementEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ErrorNotifyBacenRepository extends CrudRepository<ErrorNotifyBacenEntity, String> {

}