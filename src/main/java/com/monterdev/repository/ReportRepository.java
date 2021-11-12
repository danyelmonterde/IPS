package com.monterdev.repository;

import com.monterdev.model.Reports;
import org.springframework.data.repository.CrudRepository;

public interface ReportRepository extends CrudRepository<Reports, Integer> {

}
