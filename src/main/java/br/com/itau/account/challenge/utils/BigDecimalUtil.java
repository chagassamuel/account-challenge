package br.com.itau.account.challenge.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BigDecimalUtil {

    public static boolean greaterThan(final BigDecimal left, final BigDecimal right) {
        return left.compareTo(right) > 0;
    }

}
