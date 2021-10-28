package com.monterdev.repository;

import com.monterdev.model.RequisitionIssueSlip;
import org.springframework.data.repository.CrudRepository;

public interface RisRepository extends CrudRepository<RequisitionIssueSlip, Integer> {
}
