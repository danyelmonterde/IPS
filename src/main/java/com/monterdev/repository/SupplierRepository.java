package com.monterdev.repository;

import com.monterdev.model.SupplierGroup;
import org.springframework.data.repository.CrudRepository;

public interface SupplierRepository extends CrudRepository<SupplierGroup, Integer> {
}
