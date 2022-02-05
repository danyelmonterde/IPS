package com.monterdev.repository;

import com.monterdev.model.Balance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BalanceRepository extends CrudRepository<Balance,Integer> {

    @Query(value = "SELECT endbalance FROM balance WHERE category=:category AND month=:month AND year=:year and category NOT IN ('PPE','SEMI','MEDICAL SUPPLIES')", nativeQuery = true)
    double getBeginningBalanceInventoryByCategory(@Param("category") String category, @Param("month") String month, @Param("year") String year);

    @Query(value = "SELECT * FROM balance WHERE category=:category AND month=:month AND year=:year AND and category NOT IN ('PPE','SEMI','MEDICAL SUPPLIES')", nativeQuery = true)
    Balance getBalances(@Param("category") String category, @Param("month") String month, @Param("year") String year);

    @Query(value = "SELECT sum(a.endbalance) FROM  balance a where year=:year and month=:month and category in (SELECT a.category_name FROM itemcategory a) and category NOT IN ('PPE','SEMI','MEDICAL SUPPLIES')", nativeQuery = true)
    double getTotalBeginningBalanceForThisMonth( @Param("month") String month, @Param("year") String year);

    @Query(value = "SELECT * FROM  balance a where year=:year and month=:month and category in (SELECT a.category_name FROM itemcategory a) and category NOT IN ('PPE','SEMI','MEDICAL SUPPLIES')", nativeQuery = true)
    List<Balance> getTotalBalancesForThisMonth(@Param("month") String month, @Param("year") String year);

    @Query(value = "SELECT * FROM balance WHERE month=:month and year=:year and category=:category and category NOT IN ('PPE','SEMI','MEDICAL SUPPLIES')" ,nativeQuery = true)
    Balance getBalanceIdOfCurrentInventoryMonth(@Param("month") String month, @Param("year") String year, @Param("category") String category);

}
