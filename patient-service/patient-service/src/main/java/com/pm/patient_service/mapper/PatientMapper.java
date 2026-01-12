package com.pm.patient_service.mapper;

import com.pm.patient_service.dto.PatientResponseDTO;
import com.pm.patient_service.model.Patient;

public class PatientMapper {
    public static PatientResponseDTO toDTO(Patient patient) {
        return new PatientResponseDTO(
            patient.getId().toString(), 
            patient.getName(), 
            patient.getEmail(), 
            patient.getAddress(), 
            patient.getDateOfBirth().toString());
    }
}
