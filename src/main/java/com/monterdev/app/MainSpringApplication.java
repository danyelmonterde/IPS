package com.monterdev.app;

import javafx.application.Application;
import nu.pattern.OpenCV;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
@Configuration
@EnableJpaRepositories(basePackages = {"com.monterdev.repository"})
@ComponentScan(basePackages = {"com.monterdev.controller", "com.monterdev.configuration", "com.monterdev.exception", "com.monterdev.mapper", "com.monterdev.util"})
@EntityScan("com.monterdev.model")
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
