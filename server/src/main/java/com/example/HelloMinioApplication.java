package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
public class HelloMinioApplication {

    public static void main(String[] args) {
        SpringApplication.run(HelloMinioApplication.class, args);
    }

}
