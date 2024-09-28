package de.vd40xu.smilebase.controller;

import de.vd40xu.smilebase.dto.PatientScheduleRequestDTO;
import de.vd40xu.smilebase.service.PatientScheduleService;
import de.vd40xu.smilebase.service.interfaces.IPatientScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
@Controller
@RequestMapping("/api")
public class PatientScheduleController {

    private final IPatientScheduleService patientScheduleService;

    public PatientScheduleController(
            PatientScheduleService patientScheduleService
    ) {
        this.patientScheduleService = patientScheduleService;
    }

    @GetMapping("/patient-schedule")
    public ResponseEntity<Object> getPatientSchedule(
            @RequestBody PatientScheduleRequestDTO requestDTO
    ) {
        try {
            return ResponseEntity.ok(patientScheduleService.getPatientSchedule(requestDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
