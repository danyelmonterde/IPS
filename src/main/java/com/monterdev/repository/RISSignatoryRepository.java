package com.monterdev.repository;

import com.monterdev.model.RequisitionIssueSlipSignatories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;

public interface RISSignatoryRepository extends JpaRepository<RequisitionIssueSlipSignatories,Integer> {

    @Procedure
    void truncateRisSignatory();
}
