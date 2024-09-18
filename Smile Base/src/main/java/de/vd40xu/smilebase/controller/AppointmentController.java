package de.vd40xu.smilebase.controller;

import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.dto.NewAppointmentDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.service.AppointmentService;
import de.vd40xu.smilebase.service.PatientService;
import de.vd40xu.smilebase.service.interfaces.IAppointmentService;
import de.vd40xu.smilebase.service.interfaces.IPatientService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/api")
public class AppointmentController {

    private final IAppointmentService appointmentService;
    private final IPatientService patientService;

    public AppointmentController(
            AppointmentService appointmentService,
            PatientService patientService) {
        this.appointmentService = appointmentService;
        this.patientService = patientService;
    }

    @GetMapping("/patients/search")
    public ResponseEntity<Patient> searchPatientByInsurance(@RequestParam String insuranceNumber) {
        Optional<Patient> patient = patientService.getPatientByInsuranceNumber(insuranceNumber);
        return patient.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/appointments/free-slots")
    public ResponseEntity<List<LocalDateTime>> getFreeSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean weekView,
            @RequestParam AppointmentType appointmentType
            ) {
        List<LocalDateTime> freeSlots = appointmentService.getAvailableAppointments(doctorId, date, appointmentType, weekView);
        return ResponseEntity.ok(freeSlots);
    }

    @PostMapping("/appointments")
    public ResponseEntity<Appointment> scheduleAppointment(@RequestBody NewAppointmentDTO appointmentDTO) {
        Appointment appointment = appointmentService.scheduleAppointment(appointmentDTO);
        return ResponseEntity.ok(appointment);
    }

    @GetMapping("/appointments/{appointmentId}")
    public ResponseEntity<Appointment> getAppointmentDetails(@PathVariable Long appointmentId) {
        Appointment appointment = appointmentService.getAppointmentById(appointmentId);
        return appointment != null ? ResponseEntity.ok(appointment) : ResponseEntity.notFound().build();
    }

    @PutMapping("/appointments")
    public ResponseEntity<Appointment> changeAppointment(
            @RequestBody AppointmentDTO appointmentDTO) {
        Appointment updatedAppointment = appointmentService.updateAppointment(appointmentDTO);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }
}
