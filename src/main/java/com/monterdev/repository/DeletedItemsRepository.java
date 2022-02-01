package com.monterdev.repository;

import com.monterdev.model.DeletedItems;
import com.monterdev.model.Item;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DeletedItemsRepository extends CrudRepository<DeletedItems, Integer> {

    @Modifying
    @Query(value = "TRUNCATE TABLE deleteditems",nativeQuery = true)
    void truncateDeletedItemsHistory();
}
