package com.monterdev.repository;

import com.monterdev.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface UserRepository extends JpaRepository<User,Integer> {

    @Query(value = "SELECT * FROM user WHERE username=:username", nativeQuery = true)
    User findByUsername(@Param("username") String username);
}
