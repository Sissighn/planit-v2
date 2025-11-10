package com.setayesh.planit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlanitApplication {

    public static void main(String[] args) {
        SpringApplication.run(PlanitApplication.class, args);
        System.out.println("PlanIt REST API is running at http://localhost:8080");
    }
}
