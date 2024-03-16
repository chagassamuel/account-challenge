package br.com.itau.account.challenge.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ResourceUtils {

    public static Object getObject(final String file, final Class clazz) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        try {
            return objectMapper.readValue(getResource(file), clazz);
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static <T> List<T> getListObject(final String file, final Class clazz) {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            final Class<T[]> arrayClass = (Class<T[]>) Class.forName("[L" + clazz.getName() + ";");
            return Arrays.asList(objectMapper.readValue(getResource(file), arrayClass));
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

    public static String getResource(final String file) {
        try (final InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
            return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (final Exception ex) {
            log.error(ex.getMessage(), ex);
            return null;
        }
    }

}
