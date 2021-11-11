package com.monterdev.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppStateConfig {

    @Bean
    public ApplicationState applicationState() {
        return new ApplicationState();
    }

}
