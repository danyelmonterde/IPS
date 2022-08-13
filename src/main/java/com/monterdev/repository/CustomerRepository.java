package com.monterdev.repository;

import com.monterdev.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;


public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
