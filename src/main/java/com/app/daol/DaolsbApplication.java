package com.app.daol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class DaolsbApplication {
    public static void main(String[] args) {
        SpringApplication.run(DaolsbApplication.class, args);
    }
}
