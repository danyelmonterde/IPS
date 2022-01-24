package com.monterdev.repository;

import com.monterdev.model.SupplierGroup;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface SupplierRepository extends CrudRepository<SupplierGroup, Integer> {

    @Query(value = "SELECT purchase_order_number FROM supplier WHERE sku=:sku AND date_created=:date_created", nativeQuery = true)
    String getPurchaseOrderNumberBySkuAndDate(@Param("sku") int sku, @Param("date_created") LocalDateTime date_created);
}
