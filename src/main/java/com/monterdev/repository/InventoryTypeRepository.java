package com.monterdev.repository;

import com.monterdev.model.InventoryType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface InventoryTypeRepository extends CrudRepository<InventoryType, Integer> {

    @Query(value = "SELECT * FROM inventorytype", nativeQuery = true)
    List<InventoryType> findAllInventoryType();
}
