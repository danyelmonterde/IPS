package com.monterdev.repository;

import com.monterdev.model.InventoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface InventoryTypeRepository extends JpaRepository<InventoryType, Integer> {

    @Query(value = "SELECT * FROM inventorytype", nativeQuery = true)
    List<InventoryType> findAllInventoryType();
}
