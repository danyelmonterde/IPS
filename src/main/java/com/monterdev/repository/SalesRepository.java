package com.monterdev.repository;

import com.monterdev.model.RisTypeFields;
import com.monterdev.model.Sales;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesRepository extends CrudRepository<Sales, Integer> {

    @Query(value = "SELECT * FROM sales WHERE control_number=:control_number", nativeQuery = true)
    List<Sales> findByControlNumber(@Param("control_number") String control_number);
}
