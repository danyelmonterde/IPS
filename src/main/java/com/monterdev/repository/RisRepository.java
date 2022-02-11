package com.monterdev.repository;

import com.monterdev.model.RequisitionIssueSlip;
import com.monterdev.model.RisType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RisRepository extends CrudRepository<RequisitionIssueSlip, Integer> {

    @Query(value = "SELECT *\n" +
            "  FROM requisitionissueslip\n" +
            " WHERE ( UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) >= UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY - INTERVAL 1 MONTH)\n" +
            "   AND (UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) <  UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY) AND ristype = :risType", nativeQuery = true)
    List<RequisitionIssueSlip> findAllRequisitionSlipByCurrentMonthOfTheSpecifiedDate (@Param("dateSpecified") String dateSpecified, @Param("risType") String risType);

    @Query(value = "SELECT ristype\n" +
            "  FROM requisitionissueslip\n" +
            " WHERE ( UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) >= UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY - INTERVAL 1 MONTH)\n" +
            "   AND (UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) <  UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY) AND ristype IN (SELECT name FROM ristypenames WHERE inventorytype = :inventorytype) GROUP BY ristype", nativeQuery = true)
    List<String> findAllRISTypeByCurrentMonthOfDateSpecifiedAndInventoryType (@Param("dateSpecified") String dateSpecified, @Param("inventorytype") String inventorytype);

    @Query(value = "SELECT name from ristypenames WHERE inventorytype=:inventorytype", nativeQuery = true)
    List<String> findAllRISTypeByInventoryType (@Param("inventorytype") String inventorytype);
}
