package com.monterdev.controller;

import com.monterdev.model.Item;
import com.monterdev.repository.ItemsRepository;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SearchEngineController {

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private MainDashboardController dashboardController;

    public void searchItemOnDashboard() {
        dashboardController.getDashboardSearchField().textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {
                List<Item> itemList = itemsRepository.findAll(hasItemName(nv).or(hasSku(nv)));
                if (!ObjectUtils.isEmpty(itemList)) {
                    setItemResultOnDashboard(itemList.get(0).getItem_name());
                }
            }
        });
    }

    private void setItemResultOnDashboard(String result) {
        dashboardController.getDashboardSearchResult().textProperty().addListener((ob, ov, nv) -> {
            if (ov != nv) {

            }
        });
        dashboardController.getDashboardSearchResult().setText(result);
    }

    private Specification<Item> hasItemName(String itemName) {
        return (item, cq, cb) -> cb.like(item.get("item_name"), "%" + itemName + "%");
    }

    private Specification<Item> hasSku(String sku) {
        return (item, cq, cb) -> cb.like(item.get("sku"), "%" + sku + "%");
    }
}
