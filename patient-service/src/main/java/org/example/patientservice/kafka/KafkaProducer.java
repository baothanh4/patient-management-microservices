package org.example.patientservice.kafka;

import org.example.patientservice.exception.GlobalExceptionHandler;
import org.example.patientservice.model.Patient;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import patient.events.PatientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaProducer {
    private final KafkaTemplate<String,byte[]> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    public KafkaProducer(KafkaTemplate<String,byte[]> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendEvent(Patient patient){
        PatientEvent event=PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();

        try{
            kafkaTemplate.send("patient",event.toByteArray());
        } catch (Exception e) {
            log.error("Error sending PatientCreated event: {}",event);
        }
    }
}
