package com.monterdev.repository;

import com.monterdev.model.Signatory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SignatoryRepository extends JpaRepository<Signatory, Integer> {

    @Query(value = "SELECT * FROM signatory WHERE role=:role AND reportid=:reportid", nativeQuery = true)
    Signatory findSignatoryByRole(@Param("role") String role,@Param("reportid") int reportid);
}
