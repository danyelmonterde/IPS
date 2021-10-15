package com.monterdev.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppStateConfig {

    @Bean
    public ApplicationState applicationState(){
        return new ApplicationState();
    }

}
