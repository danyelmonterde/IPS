package com.monterdev.repository;

import com.monterdev.model.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<ItemCategory, Integer> {

    @Query(value = "SELECT * FROM itemcategory ORDER BY id asc LIMIT 1 ", nativeQuery = true)
    ItemCategory findFirstItemCategory();
}
