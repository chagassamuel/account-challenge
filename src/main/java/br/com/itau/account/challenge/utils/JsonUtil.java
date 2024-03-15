package br.com.itau.account.challenge.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtil {

    public static String objectToJson(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (final JsonProcessingException ex) {
            return null;
        }
    }

}
