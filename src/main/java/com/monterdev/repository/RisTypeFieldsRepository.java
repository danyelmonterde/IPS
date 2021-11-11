package com.monterdev.repository;

import com.monterdev.model.ItemCategory;
import com.monterdev.model.RisTypeFields;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RisTypeFieldsRepository extends CrudRepository<RisTypeFields, Integer> {

    @Query(value = "SELECT * FROM ristypefields WHERE control_number=:control_number", nativeQuery = true)
    List<RisTypeFields> findByControlNumber(@Param("control_number") String control_number);
}
