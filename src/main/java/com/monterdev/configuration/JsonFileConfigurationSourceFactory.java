package com.monterdev.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;

import java.io.IOException;
import java.util.Map;

public class JsonFileConfigurationSourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String s, EncodedResource encodedResource) throws IOException {
        Map readValue = new ObjectMapper()
                .readValue(encodedResource.getInputStream(), Map.class);
        return new MapPropertySource("json-property", readValue);
    }
}
