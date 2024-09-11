package de.vd40xu.smilebase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class SmileBaseApplicationTests {

    @Test
    @DisplayName("Unit > Context loads")
    void test1() {
        Assertions.assertDoesNotThrow( () -> { } );
    }

    @Configuration static class Config { }
}
