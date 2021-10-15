package com.monterdev.repository;

import com.monterdev.model.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends CrudRepository<Category, Integer> {

    @Query(value = "SELECT * FROM category WHERE category_name=:category", nativeQuery = true)
    Category findByCategory(@Param("category") String category);
}
