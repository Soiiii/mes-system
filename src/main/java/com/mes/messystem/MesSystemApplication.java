package com.mes.messystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MesSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesSystemApplication.class, args);
    }

}
