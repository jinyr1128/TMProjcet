package com.tmproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableScheduling
@SpringBootApplication
public class TmProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TmProjectApplication.class, args);
    }

}
