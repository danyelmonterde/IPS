package com.monterdev.configuration;

import com.monterdev.model.Category;
import com.monterdev.model.Item;
import com.monterdev.model.SubCategoryDetail;
import com.monterdev.model.SubCategoryHeader;
import com.monterdev.repository.CategoryRepository;
import com.monterdev.repository.ItemsRepository;
import com.monterdev.repository.SubCategoryDetailsRepository;
import com.monterdev.repository.SubCategoryHeaderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class OnloadConfig {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubCategoryDetailsRepository subCategoryDetailsRepository;

    @Autowired
    private SubCategoryHeaderRepository subCategoryHeaderRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Bean
    public Item item() {
        //set default category for loading
        return new Item().builder().item_name("").build();
    }

    @Bean
    public Iterable<Category> category() {
        return categoryRepository.findAll();
    }

    @Bean
    public Iterable<SubCategoryHeader> subCategoryHeader() {
        return subCategoryHeaderRepository.findAll();
    }

    @Bean
    public Iterable<SubCategoryDetail> subCategoryDetail() {
        return subCategoryDetailsRepository.findAll();
    }

    @Bean
    @Qualifier("itemLists")
    public List<Item> itemLists(){
        List<Item> itemLists = new ArrayList<>();
        Iterable<Item> initialItemList = itemsRepository.findAll();
        itemLists.clear();
        initialItemList.forEach(itemLists::add);
        return itemLists;
    }

}
