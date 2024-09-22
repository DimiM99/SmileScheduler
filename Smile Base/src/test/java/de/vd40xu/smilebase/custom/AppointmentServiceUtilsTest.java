package de.vd40xu.smilebase.custom;

import de.vd40xu.smilebase.service.utility.AppointmentServiceUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AppointmentServiceUtilsTest {

    @Test
    @DisplayName("Unit > Test AppointmentServiceUtils constructor")
    void test() {
        assertThrows(IllegalStateException.class, AppointmentServiceUtils::new);
    }
}