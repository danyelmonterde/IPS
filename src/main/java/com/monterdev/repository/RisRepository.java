package com.monterdev.repository;

import com.monterdev.model.RequisitionIssueSlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RisRepository extends JpaRepository<RequisitionIssueSlip, Integer> {

    @Query(value = "SELECT *\n" +
            "  FROM requisitionissueslip\n" +
            " WHERE ( UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) >= UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY - INTERVAL 1 MONTH)\n" +
            "   AND (UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) <  UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY) AND purpose = :risType", nativeQuery = true)
    List<RequisitionIssueSlip> findAllRequisitionSlipByCurrentMonthOfTheSpecifiedDate(@Param("dateSpecified") String dateSpecified, @Param("risType") String risType);

    @Query(value = "SELECT purpose\n" +
            "  FROM requisitionissueslip\n" +
            " WHERE ( UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) >= UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY - INTERVAL 1 MONTH)\n" +
            "   AND (UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) <  UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY) AND purpose IN (SELECT name FROM ristypenames WHERE inventorytype = :inventorytype) GROUP BY purpose", nativeQuery = true)
    List<String> findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType(@Param("dateSpecified") String dateSpecified, @Param("inventorytype") String inventorytype);

    @Query(value = "SELECT name from ristypenames WHERE inventorytype=:inventorytype", nativeQuery = true)
    List<String> findAllRISTypeByInventoryType(@Param("inventorytype") String inventorytype);
}
