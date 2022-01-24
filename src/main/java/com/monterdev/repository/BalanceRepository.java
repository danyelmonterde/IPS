package com.monterdev.repository;

import com.monterdev.model.Balance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface BalanceRepository extends CrudRepository<Balance,Integer> {

    @Query(value = "SELECT endbalance FROM balance WHERE category=:category AND month=:month AND year=:year", nativeQuery = true)
    double getBeginningBalanceInventoryByCategory(@Param("category") String category, @Param("month") String month, @Param("year") String year);

}
