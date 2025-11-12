package org.example.patientservice.service;


import org.example.patientservice.dto.PatientRequestDTO;
import org.example.patientservice.dto.PatientResponseDTO;
import org.example.patientservice.exception.EmailAlreadyExistsException;
import org.example.patientservice.exception.PatientNotFoundException;
import org.example.patientservice.grpc.BillingServiceGrpcClient;
import org.example.patientservice.kafka.KafkaProducer;
import org.example.patientservice.mapper.PatientMapper;
import org.example.patientservice.model.Patient;
import org.example.patientservice.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patientRepository;
    private final BillingServiceGrpcClient billingServiceGrpcClient;
    private final KafkaProducer kafkaProducer;
    public PatientService(PatientRepository patientRepository, BillingServiceGrpcClient billingServiceGrpcClient, KafkaProducer kafkaProducer) {
        this.patientRepository = patientRepository;
        this.billingServiceGrpcClient = billingServiceGrpcClient;
        this.kafkaProducer = kafkaProducer;
    }

    public List<PatientResponseDTO> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(PatientMapper::toDTO).toList();
    }

    public PatientResponseDTO createPatient(PatientRequestDTO patientRequestDTO) {
        if(patientRepository.existsByEmail(patientRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A patient with an email already exists "+ patientRequestDTO.getEmail());
        }

        Patient patient=patientRepository.save(PatientMapper.toModel(patientRequestDTO));

        billingServiceGrpcClient.createBillingAccount(patient.getId().toString(),patient.getName(),patient.getEmail());

        kafkaProducer.sendEvent(patient);

        return PatientMapper.toDTO(patient);
    }

    public PatientResponseDTO updatePatient(UUID id,PatientRequestDTO dto){
        Patient patient= patientRepository.findById(id).orElseThrow(()->new PatientNotFoundException("Patinet not found with id: "+id));
        if(patientRepository.existsByEmailAndIdNot(dto.getEmail(),id)) {
            throw new EmailAlreadyExistsException("A patient with an email already exists "+ dto.getEmail());
        }
        patient.setName(dto.getName());
        patient.setAddress(dto.getAddress());
        patient.setEmail(dto.getEmail());
        patient.setDateOfBirth(LocalDate.parse(dto.getDateOfBirth()));

        Patient updatedPatient=patientRepository.save(patient);
        return PatientMapper.toDTO(updatedPatient);
    }

    public void deletePatient(UUID id){
        patientRepository.deleteById(id);
    }
}
