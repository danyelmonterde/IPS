package com.monterdev.repository;

import com.monterdev.model.SubCategoryHeader;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SubCategoryHeaderRepository extends CrudRepository<SubCategoryHeader, Integer> {

    @Query(value = "SELECT * FROM subcategoryheader WHERE sub_category_header=:sub_category_header", nativeQuery = true)
    SubCategoryHeader findBySubCategoryHeader(@Param("sub_category_header") String sub_category_header);

    @Query(value = "SELECT * FROM subcategoryheader WHERE category=:category", nativeQuery = true)
    SubCategoryHeader findByCategory(@Param("category") String category);

}
