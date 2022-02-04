package com.monterdev.repository;

import com.monterdev.model.DeletedItems;
import com.monterdev.model.Item;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeletedItemsRepository extends CrudRepository<DeletedItems, Integer> {

    @Procedure
    void truncateDeletedItems();
}
