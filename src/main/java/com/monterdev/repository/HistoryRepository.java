package com.monterdev.repository;

import com.monterdev.model.History;
import org.springframework.data.repository.CrudRepository;

public interface HistoryRepository extends CrudRepository<History, Integer> {
}
