package com.monterdev.repository;

import com.monterdev.model.Sales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalesRepository extends JpaRepository<Sales, Integer> {

    @Query(value = "SELECT * FROM sales WHERE control_number=:control_number", nativeQuery = true)
    List<Sales> findByControlNumber(@Param("control_number") String control_number);

    @Query(value = "SELECT round(sum(total_cost),2) FROM sales WHERE control_number IN (SELECT control_number FROM requisitionissueslip\n" +
            "WHERE ( UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) >= UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY - INTERVAL 1 MONTH)\n" +
            "AND (UNIX_TIMESTAMP(STR_TO_DATE(requisitionissueslip.date_transacted, '%d-%b-%Y %s'))) <  UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%d-%b-%Y %s')) + INTERVAL 1 DAY) AND purpose =:ristype)", nativeQuery = true)
    String computeTotalCostPerRISType(@Param("ristype") String ristype, @Param("dateSpecified") String dateSpecified);
}
