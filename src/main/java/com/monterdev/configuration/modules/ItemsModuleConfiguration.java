package com.monterdev.configuration.modules;

import com.monterdev.controller.ItemListController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemsModuleConfiguration {

    @Bean(name = "defaultItemConfiguration")
    public ItemListController itemListController() {
        return new ItemListController();
    }
}
