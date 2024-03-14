package br.com.itau.account.challenge.mapper;

import br.com.itau.account.challenge.integration.hw.domain.response.HWResponse;
import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HWMapper {

    HWResponse toHWResponse(final PersonCacheEntity personCacheEntity);

    PersonCacheEntity toPersonCacheEntity(final HWResponse hwResponse);

}
