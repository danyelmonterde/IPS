package com.monterdev.configuration;

import com.monterdev.model.CustomConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
@PropertySource(
        value = "classpath:global-config.json",
        factory = JsonFileConfigurationSourceFactory.class
)
public class JsonFileConfiguration {


    private List<CustomConfig> customConfigurations;

}
