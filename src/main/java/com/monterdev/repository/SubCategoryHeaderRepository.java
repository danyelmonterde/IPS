package com.monterdev.repository;

import com.monterdev.model.ItemSubCategoryHeader;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SubCategoryHeaderRepository extends CrudRepository<ItemSubCategoryHeader, Integer> {

    @Query(value = "SELECT * FROM subcategoryheader WHERE sub_category_header=:sub_category_header", nativeQuery = true)
    ItemSubCategoryHeader findBySubCategoryHeader(@Param("sub_category_header") String sub_category_header);

    @Query(value = "SELECT * FROM subcategoryheader WHERE category=:category", nativeQuery = true)
    ItemSubCategoryHeader findByCategory(@Param("category") String category);

}
