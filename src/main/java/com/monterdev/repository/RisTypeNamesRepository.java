package com.monterdev.repository;

import com.monterdev.model.RisTypeNames;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RisTypeNamesRepository extends CrudRepository<RisTypeNames,Integer> {

    @Query(value = "SELECT * FROM ristypenames", nativeQuery = true)
    List<RisTypeNames> findAllRisTypeNames();

    @Query(value = "SELECT * FROM ristypenames WHERE name=:type", nativeQuery = true)
    RisTypeNames findRisNameByType(@Param("type") String type);


}
