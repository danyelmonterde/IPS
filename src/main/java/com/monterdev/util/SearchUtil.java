package com.monterdev.util;

import com.monterdev.model.Item;
import com.monterdev.repository.ItemsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

import static sun.misc.MessageUtils.where;

public class SearchUtil {

    @Autowired
    private ItemsRepository itemsRepository;

    public SearchUtil(){

    }

    public SearchUtil(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public void searchItem(String text, List<Item> resultsList, List<String> responseList, String searchType) {
        responseList.clear();
        Pageable initialItemPageList = PageRequest.of(0, 10);
        Page<Item> initialItemPage = itemsRepository.findAll(hasItemName(text), initialItemPageList);
        initialItemPage.stream().forEach(e->{
            responseList.add(e.getItem_name());
        });

    }

    private Specification<Item> hasItemName(String itemName) {
        return (item, cq, cb) -> cb.like(item.get("item_name"), "%" + itemName + "%");
    }


}
