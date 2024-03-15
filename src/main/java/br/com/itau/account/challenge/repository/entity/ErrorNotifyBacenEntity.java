package br.com.itau.account.challenge.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tb_itau_error_notify_bacen")
public class ErrorNotifyBacenEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_error_notify_bacen")
    private String idErrorNotifyBacen;

    @Column(name = "payload", length = 2000)
    private String payload;

    @Column(name = "date_time_inclusion")
    private final LocalDateTime dateTimeInclusion = LocalDateTime.now();

}