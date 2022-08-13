package com.monterdev.repository;

import com.monterdev.model.ReportNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReportNamesRepository extends JpaRepository<ReportNames, Integer> {

    @Query(value = "SELECT * FROM reportnames", nativeQuery = true)
    List<ReportNames> findAllAvailableReports();

    @Query(value = "SELECT description FROM reportnames where name=:name", nativeQuery = true)
    String findReportDescription(@Param("name") String name);

    @Query(value = "SELECT * FROM reportnames where id=:id", nativeQuery = true)
    ReportNames findReportNameById(@Param("id") int id);
}
