package com.monterdev.repository;

import com.monterdev.model.StockAdjustment;
import org.springframework.data.repository.CrudRepository;

public interface StockAdjustmentRepository extends CrudRepository<StockAdjustment, Integer> {
}
