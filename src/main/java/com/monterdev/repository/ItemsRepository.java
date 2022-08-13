package com.monterdev.repository;

import com.monterdev.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


public interface ItemsRepository extends JpaRepository<Item, Integer>, JpaSpecificationExecutor<Item> {

    Page<Item> findAll(Pageable pageable);

    @Query(value = "SELECT * FROM item WHERE tag is NULL AND sku:=sku", nativeQuery = true)
    Item updateItem(@Param("sku") int sku);

    @Query(value = "SELECT count(*) FROM item WHERE in_stock=0", nativeQuery = true)
    int getOutOfStockItems();

    @Query(value = "SELECT count(*) FROM item WHERE in_stock=low_stock", nativeQuery = true)
    int getLowStockItems();

    @Query(value = "select sum(cost * in_stock) as 'sum'  from item WHERE item_category=:itemCategory", nativeQuery = true)
    String getEndBalanceByInventoryType(@Param("itemCategory") String itemCategory);

    @Procedure
    void truncateItems();
}
