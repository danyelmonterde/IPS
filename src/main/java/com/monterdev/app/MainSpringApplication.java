package com.monterdev.app;

import javafx.application.Application;
import nu.pattern.OpenCV;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
@ComponentScan(basePackages = "com.monterdev.controller, com.monterdev.serviceimpl, com.monterdev.configuration, com.monterdev.mapper,com.monterdev.service,com.monterdev.util")
@EnableJpaRepositories(basePackages = "com.monterdev.repository")
@EntityScan(basePackages = "com.monterdev.model")
public class MainSpringApplication {


    public static void main(String[] args) {
        OpenCV.loadShared();
        try {
            Process proc = Runtime.getRuntime().exec("java -jar SplashScreen.jar");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Application.launch(MainJavaFXApplication.class, args);
    }


}
