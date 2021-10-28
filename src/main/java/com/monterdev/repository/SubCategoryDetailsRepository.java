package com.monterdev.repository;

import com.monterdev.model.ItemSubCategoryDetail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SubCategoryDetailsRepository extends CrudRepository<ItemSubCategoryDetail, Integer> {

    @Query(value = "SELECT * FROM subcategorydetail WHERE sub_category_detail=:sub_category_detail", nativeQuery = true)
    ItemSubCategoryDetail findBySubCategoryDetail(@Param("sub_category_detail") String sub_category_detail);
}
