package com.monterdev.repository;

import com.monterdev.model.DeletedItems;
import com.monterdev.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface DeletedItemsRepository extends CrudRepository<DeletedItems, Integer> {

}
