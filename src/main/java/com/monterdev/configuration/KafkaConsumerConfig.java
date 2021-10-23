package com.monterdev.configuration;

import com.fasterxml.jackson.databind.JavaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.monterdev.model.DeletedItems;
import com.monterdev.model.Item;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public ConsumerFactory<String, List<Item>> itemsConsumerFactory(){
        System.out.println("pumasok dito sa itemsConsumerFactory");
        Map<String,Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"myGroup");

        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory().constructParametricType(List.class, Item.class);
        return new DefaultKafkaConsumerFactory<>(config,new StringDeserializer(), new JsonDeserializer<List<Item>>(type, om, false));
    }

    @Bean
    public ConsumerFactory<String, List<DeletedItems>> deletedItemConsumerFactory(){
        System.out.println("pumasok dito sa deletedItemConsumerFactory");
        Map<String,Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9092");
        config.put(ConsumerConfig.GROUP_ID_CONFIG,"myGroup");

        ObjectMapper om = new ObjectMapper();
        JavaType type = om.getTypeFactory().constructParametricType(List.class, DeletedItems.class);
        return new DefaultKafkaConsumerFactory<>(config,new StringDeserializer(), new JsonDeserializer<List<DeletedItems>>(type, om, false));
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<Item>> itemListener(){
        System.out.println("pumasok dito sa itemListener");
        ConcurrentKafkaListenerContainerFactory<String, List<Item>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(itemsConsumerFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, List<DeletedItems>> deletedItemListener(){
        System.out.println("pumasok dito sa deletedItemListener");
        ConcurrentKafkaListenerContainerFactory<String, List<DeletedItems>> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(deletedItemConsumerFactory());
        return factory;
    }

}
