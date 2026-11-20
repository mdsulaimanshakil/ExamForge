package com.examforge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ExamForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamForgeApplication.class, args);
    }
}
