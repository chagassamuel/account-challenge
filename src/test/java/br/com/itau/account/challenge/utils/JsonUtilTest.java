package br.com.itau.account.challenge.utils;

import br.com.itau.account.challenge.repository.entity.AccountEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
public class JsonUtilTest {

    @Test
    public void objectToJsonTest() throws JsonProcessingException {
        final AccountEntity accountEntity = JsonHandlerMock.getAccountEntity();
        final ObjectMapper objectMapper = new ObjectMapper();

        assertEquals(objectMapper.writeValueAsString(accountEntity), JsonUtil.objectToJson(accountEntity));
    }

    @Test
    public void objectToJsonExceptionTest() {
        assertNull(JsonUtil.objectToJson(new Object()));
    }

}