package com.bytecard;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest
@DisplayName("ByteCardApplication")
class ByteCardApplicationTest {

    @Test
    @DisplayName("Deve subir o contexto da aplicação sem falhas")
    void contextLoads() {
        assertThat(true).isTrue();
    }

    @Test
    @DisplayName("Deve executar o método main sem lançar exceções")
    void deveExecutarMetodoMainSemExcecao() {
        assertThatCode(() -> ByteCardApplication.main(new String[]{}))
                .doesNotThrowAnyException();
    }
}
