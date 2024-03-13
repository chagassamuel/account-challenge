package br.com.itau.account.challenge.repository.entity;

import br.com.itau.account.challenge.repository.domain.StatementTypeEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "tb_itau_statement")
public class StatementEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_statement")
    private String idStatement;

    @Column(name = "value_statement")
    private BigDecimal value;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private StatementTypeEnum type;

    @Column(name = "effective_date")
    private final LocalDate effectiveDate = LocalDate.now();

    @Column(name = "id_account_involved")
    private String idAccountInvolved;

    @ManyToOne
    @JoinColumn(name = "id_account", referencedColumnName = "id_account", updatable = false)
    private AccountEntity account;

}