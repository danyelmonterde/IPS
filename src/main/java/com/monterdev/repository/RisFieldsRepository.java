package com.monterdev.repository;

import com.monterdev.model.RisFields;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;


public interface RisFieldsRepository extends JpaRepository<RisFields, Integer> {
}
