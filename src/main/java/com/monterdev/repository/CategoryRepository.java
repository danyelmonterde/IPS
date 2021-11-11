package com.monterdev.repository;

import com.monterdev.model.ItemCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends CrudRepository<ItemCategory, Integer> {

    @Query(value = "SELECT * FROM itemcategory WHERE category_name=:category", nativeQuery = true)
    ItemCategory findByCategory(@Param("category") String category);
}
