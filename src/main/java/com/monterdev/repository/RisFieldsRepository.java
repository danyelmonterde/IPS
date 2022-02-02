package com.monterdev.repository;

import com.monterdev.model.RisFields;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface RisFieldsRepository extends JpaRepository<RisFields, Integer> {
}
