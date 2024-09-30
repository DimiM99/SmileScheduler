package de.vd40xu.smilebase.controller;

import de.vd40xu.smilebase.dto.AppointmentDTO;
import de.vd40xu.smilebase.dto.NewAppointmentDTO;
import de.vd40xu.smilebase.model.Appointment;
import de.vd40xu.smilebase.model.User;
import de.vd40xu.smilebase.model.emuns.AppointmentType;
import de.vd40xu.smilebase.service.AppointmentService;
import de.vd40xu.smilebase.service.interfaces.IAppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/api")
public class AppointmentController {

    private final IAppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService
    ) {
        this.appointmentService = appointmentService;
    }

    @GetMapping("/appointments/free-slots")
    public ResponseEntity<Object> getFreeSlots(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean weekView,
            @RequestParam AppointmentType appointmentType
            ) {
        try {
            List<LocalDateTime> freeSlots = appointmentService.getAvailableAppointments(doctorId, date, appointmentType, weekView);
            return ResponseEntity.ok(freeSlots);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/appointments/doctors")
    public ResponseEntity<Object> getDoctors() {
        List<User> doctors = appointmentService.getDoctors();
        return ResponseEntity.ok(doctors);
    }

    @PostMapping("/appointments")
    public ResponseEntity<Object> scheduleAppointment(@RequestBody NewAppointmentDTO appointmentDTO) {
        try {
            Appointment appointment = appointmentService.scheduleAppointment(appointmentDTO);
            return ResponseEntity.ok(appointment);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/appointments")
    public ResponseEntity<Object> getAppointmentDetails(@RequestParam Long appointmentId) {
        try {
            Appointment appointment = appointmentService.getAppointmentById(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The appointment with the provided id does not exist");
        }
    }

     @GetMapping("/appointments/booked")
     public ResponseEntity<Object> getAppointmentsForDoc(
            @RequestParam Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "false") boolean weekView
     ) {
        try {
            List<Appointment> appointments = appointmentService.getAppointmentsForDoctor(doctorId, date, weekView);
            return ResponseEntity.ok(appointments);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
     }

    @PutMapping("/appointments")
    public ResponseEntity<Object> changeAppointment(
            @RequestBody AppointmentDTO appointmentDTO) {
        try {
            Appointment updatedAppointment = appointmentService.updateAppointment(appointmentDTO);
            return ResponseEntity.ok(updatedAppointment);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("The appointment with the provided id does not exist");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.ok().build();
    }
}
