package br.com.itau.account.challenge.repository.entity.cache;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

@Getter
@Builder
public class PersonCacheEntity implements Serializable {

    private String idPerson;
    private String fullname;

}
