package org.example.patientservice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PatientServiceApplication {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()                   // không lỗi nếu không có .env
                .load();

        dotenv.entries().forEach(entry ->
                System.out.println("ENV LOAD: " + entry.getKey() + " = " + entry.getValue())
        );

        SpringApplication.run(PatientServiceApplication.class, args);
    }

}
