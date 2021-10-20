package com.monterdev.app;

import javafx.application.Application;
import nu.pattern.OpenCV;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.monterdev.controller, com.monterdev.serviceimpl, com.monterdev.configuration, com.monterdev.mapper,com.monterdev.service,com.monterdev.util")
@EnableAutoConfiguration(exclude = {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
@EnableJpaRepositories(basePackages = "com.monterdev.repository")
@EntityScan(basePackages="com.monterdev.model")
public class MainSpringApplication {

    public static void main(String[] args) {
        OpenCV.loadShared();
        Application.launch(MainJavaFXApplication.class, args);
    }
}
