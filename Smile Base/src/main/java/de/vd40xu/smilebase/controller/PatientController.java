package de.vd40xu.smilebase.controller;

import de.vd40xu.smilebase.dto.PatientDTO;
import de.vd40xu.smilebase.model.Patient;
import de.vd40xu.smilebase.service.PatientService;
import de.vd40xu.smilebase.service.interfaces.IPatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api")
public class PatientController {

    private final IPatientService patientService;

    public PatientController(
            PatientService patientService
    ) {
        this.patientService = patientService;
    }

    @GetMapping("/patients/search")
    public ResponseEntity<Object> searchPatientByInsurance(@RequestParam String insuranceNumber) {
        List<Patient> res = patientService.getPatientByInsuranceNumber(insuranceNumber);
        return ResponseEntity.ok().body(res);
    }

    @PutMapping("/patients")
    public ResponseEntity<Object> updatePatient(
            @RequestBody PatientDTO patient
    ) {
        try {
            Patient updatedPatient = patientService.updatePatient(patient);
            return ResponseEntity.ok(updatedPatient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
