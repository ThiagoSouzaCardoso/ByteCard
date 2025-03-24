package com.bytecard.adapter.out.persistence.converter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

class YearMonthConverterTest {

    private YearMonthConverter converter;

    @BeforeEach
    void setUp() {
        converter = new YearMonthConverter();
    }

    @Test
    void deveConverterYearMonthParaString() {
        YearMonth yearMonth = YearMonth.of(2025, 3);

        String resultado = converter.convertToDatabaseColumn(yearMonth);

        assertThat(resultado).isEqualTo("2025-03");
    }

    @Test
    void deveRetornarNullAoConverterYearMonthNulo() {
        String resultado = converter.convertToDatabaseColumn(null);

        assertThat(resultado).isNull();
    }

    @Test
    void deveConverterStringParaYearMonth() {
        String data = "2024-12";

        YearMonth resultado = converter.convertToEntityAttribute(data);

        assertThat(resultado).isEqualTo(YearMonth.of(2024, 12));
    }

    @Test
    void deveRetornarNullAoConverterStringNula() {
        YearMonth resultado = converter.convertToEntityAttribute(null);

        assertThat(resultado).isNull();
    }

    @Test
    void deveRetornarNullAoConverterStringVazia() {
        YearMonth resultado = converter.convertToEntityAttribute("");

        assertThat(resultado).isNull();
    }
}

