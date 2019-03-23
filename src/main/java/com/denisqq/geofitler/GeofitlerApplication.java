package com.denisqq.geofitler;

import com.denisqq.geofitler.rest.FilterController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan(basePackages = {"com.denisqq"})
@Configuration
public class GeofitlerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeofitlerApplication.class, args);
    }

}
