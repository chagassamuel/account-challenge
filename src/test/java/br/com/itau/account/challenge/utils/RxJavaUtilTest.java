package br.com.itau.account.challenge.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class RxJavaUtilTest {

    @InjectMocks
    private RxJavaUtil rxJavaUtil;

    @Test
    public void objectToJsonExceptionTest() {
        assertNotNull(rxJavaUtil.getObservableParallel());
    }

}
