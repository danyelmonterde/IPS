package com.monterdev.repository;

import com.monterdev.model.DeletedItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;


public interface DeletedItemsRepository extends JpaRepository<DeletedItems, Integer> {

    @Procedure
    void truncateDeletedItems();
}
