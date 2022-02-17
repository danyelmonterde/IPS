package com.monterdev.repository;

import com.monterdev.model.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Integer> {

    @Query(value = "SELECT * FROM purchaseorder", nativeQuery = true)
    List<PurchaseOrder> findAllPurchaseOrder();

    @Query(value = "SELECT *\n" +
            "  FROM purchaseorder\n" +
            " WHERE ( UNIX_TIMESTAMP(STR_TO_DATE(purchaseorder.datecreated, '%Y-%m-%d %s'))) >= UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%Y-%m-%d %s')) + INTERVAL 1 DAY - INTERVAL 1 MONTH)\n" +
            "   AND (UNIX_TIMESTAMP(STR_TO_DATE(purchaseorder.datecreated, '%Y-%m-%d %s'))) <  UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%Y-%m-%d %s')) + INTERVAL 1 DAY) AND item_category = :item_category", nativeQuery = true)
    List<PurchaseOrder> findPurchaseOrderByItemCategory(@Param("dateSpecified") String dateSpecified, @Param("item_category") String item_category);

    @Query(value = "SELECT sum(amount)\n" +
            "  FROM purchaseorder\n" +
            " WHERE ( UNIX_TIMESTAMP(STR_TO_DATE(purchaseorder.datecreated, '%Y-%m-%d %s'))) >= UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%Y-%m-%d %s')) + INTERVAL 1 DAY - INTERVAL 1 MONTH)\n" +
            "   AND (UNIX_TIMESTAMP(STR_TO_DATE(purchaseorder.datecreated, '%Y-%m-%d %s'))) <  UNIX_TIMESTAMP(LAST_DAY(STR_TO_DATE(:dateSpecified, '%Y-%m-%d %s')) + INTERVAL 1 DAY) AND item_category = :item_category", nativeQuery = true)
    double computeSumOfAllPurchasesForThisMonth(@Param("dateSpecified") String dateSpecified, @Param("item_category") String item_category);
}
