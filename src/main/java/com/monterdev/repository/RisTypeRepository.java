package com.monterdev.repository;

import com.monterdev.model.RisType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RisTypeRepository extends JpaRepository<RisType, Integer> {

    @Query(value = "SELECT * FROM ristype WHERE ristype=:risType", nativeQuery = true)
    List<RisType> findByRisType(@Param("risType") String risType);

}
