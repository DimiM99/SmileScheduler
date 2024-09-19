package de.vd40xu.smilebase.repository.unit;

import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.repository.AppointmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentRepositoryTest {

    @Mock private AppointmentRepository appointmentRepository;

    @Test
    @DisplayName("Unit > should find appointments by doctor id")
    void test1() {
        Long doctorId = 1L;
        List<Appointment> expectedAppointments = Arrays.asList(new Appointment(), new Appointment());
        when(appointmentRepository.findByDoctorId(doctorId)).thenReturn(expectedAppointments);

        List<Appointment> result = appointmentRepository.findByDoctorId(doctorId);

        assertEquals(2, result.size());
        verify(appointmentRepository, times(1)).findByDoctorId(doctorId);
    }

    @Test
    @DisplayName("Unit > should find appointments by doctor id and start between")
    void test2() {
        Long doctorId = 1L;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(1);
        List<Appointment> expectedAppointments = Arrays.asList(new Appointment(), new Appointment(), new Appointment());
        when(appointmentRepository.findByDoctorIdAndStartBetween(doctorId, start, end)).thenReturn(expectedAppointments);

        List<Appointment> result = appointmentRepository.findByDoctorIdAndStartBetween(doctorId, start, end);

        assertEquals(3, result.size());
        verify(appointmentRepository, times(1)).findByDoctorIdAndStartBetween(doctorId, start, end);
    }

    @Test
    @DisplayName("Unit > should find appointments by patient id")
    void test3() {
        Long patientId = 1L;
        List<Appointment> expectedAppointments = List.of(new Appointment());
        when(appointmentRepository.findByPatientId(patientId)).thenReturn(expectedAppointments);

        List<Appointment> result = appointmentRepository.findByPatientId(patientId);

        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByPatientId(patientId);
    }
}
