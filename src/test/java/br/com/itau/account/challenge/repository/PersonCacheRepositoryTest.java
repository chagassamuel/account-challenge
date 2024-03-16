package br.com.itau.account.challenge.repository;

import br.com.itau.account.challenge.repository.entity.cache.PersonCacheEntity;
import br.com.itau.account.challenge.utils.JsonHandlerMock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PersonCacheRepositoryTest {

    @InjectMocks
    private PersonCacheRepository personCacheRepository;

    @Mock
    private RedisTemplate<String, PersonCacheEntity> redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    private PersonCacheEntity personCacheEntity;

    @BeforeEach
    public void setup() {
        personCacheEntity = JsonHandlerMock.getPersonCacheEntity();
    }

    @Test
    public void saveTest() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(any(), any());

        personCacheRepository.save(personCacheEntity);

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(0)).get(any());
        verify(valueOperations, times(1)).set(any(), any());
    }

    @Test
    public void findByIdTest() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(any())).thenReturn(personCacheEntity);

        final Optional<PersonCacheEntity> response = personCacheRepository.findById(personCacheEntity.idPerson());

        assertTrue(response.isPresent());
        assertEquals(personCacheEntity.idPerson(), response.get().idPerson());
        assertEquals(personCacheEntity.fullname(), response.get().fullname());

        verify(redisTemplate, times(1)).opsForValue();
        verify(valueOperations, times(1)).get(any());
        verify(valueOperations, times(0)).set(any(), any());
    }

    @Test
    public void saveFallbackTest() {
        personCacheRepository.saveFallback(new Throwable());

        verify(redisTemplate, times(0)).opsForValue();
        verify(valueOperations, times(0)).get(any());
        verify(valueOperations, times(0)).set(any(), any());
    }

    @Test
    public void findFallbackTest() {
        final Optional<PersonCacheEntity> response = personCacheRepository.findFallback(new Throwable());

        assertTrue(response.isEmpty());

        verify(redisTemplate, times(0)).opsForValue();
        verify(valueOperations, times(0)).get(any());
        verify(valueOperations, times(0)).set(any(), any());
    }

}