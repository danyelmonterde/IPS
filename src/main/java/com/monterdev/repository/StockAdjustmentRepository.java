package com.monterdev.repository;

import com.monterdev.model.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Integer> {
}
