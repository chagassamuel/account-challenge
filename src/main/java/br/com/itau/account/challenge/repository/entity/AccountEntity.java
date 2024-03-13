package br.com.itau.account.challenge.repository.entity;

import br.com.itau.account.challenge.repository.domain.AccountStatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "tb_itau_account")
public class AccountEntity implements Serializable {

    @Id
    @Column(name = "id_account")
    private String idAccount;

    @Column(name = "id_person")
    private String idPerson;

    @Column(name = "company_code")
    private String companyCode;

    @Column(name = "agency_number")
    private String agencyNumber;

    @Column(name = "account_number")
    private String account_number;

    @Column(name = "check_digit")
    private String checkDigit;

    @Column(name = "balance")
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AccountStatusEnum status;

//    @OneToMany(
//            mappedBy = "originAccount",
//            cascade = CascadeType.ALL,
//            targetEntity = StatementEntity.class,
//            fetch = FetchType.LAZY
//    )
//    private List<StatementEntity> statement;

}