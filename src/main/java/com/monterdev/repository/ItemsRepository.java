package com.monterdev.repository;

import com.monterdev.model.Item;
import org.springframework.data.repository.CrudRepository;

public interface ItemsRepository extends CrudRepository<Item, Integer> {

}
