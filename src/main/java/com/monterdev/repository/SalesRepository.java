package com.monterdev.repository;

import com.monterdev.model.Sales;
import org.springframework.data.repository.CrudRepository;

public interface SalesRepository extends CrudRepository<Sales, Integer> {
}
