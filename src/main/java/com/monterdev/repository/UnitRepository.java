package com.monterdev.repository;

import com.monterdev.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UnitRepository extends JpaRepository<Unit, Integer> {

    @Query(value = "SELECT * FROM unit", nativeQuery = true)
    List<Unit> findAllItemUnits();
}
