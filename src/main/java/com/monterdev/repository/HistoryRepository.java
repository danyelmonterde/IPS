package com.monterdev.repository;

import com.monterdev.model.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface HistoryRepository extends JpaRepository<History, Integer>, JpaSpecificationExecutor<History> {
    Page<History> findAll(Pageable pageable);
}
