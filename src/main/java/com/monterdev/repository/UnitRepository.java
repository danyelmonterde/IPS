package com.monterdev.repository;

import com.monterdev.model.Unit;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UnitRepository extends CrudRepository<Unit, Integer> {

    @Query(value = "SELECT * FROM unit", nativeQuery = true)
    List<Unit> findAllItemUnits();
}
