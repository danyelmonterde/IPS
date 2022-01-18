package com.monterdev.repository;

import com.monterdev.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends CrudRepository<User,Integer> {

    @Query(value = "SELECT password FROM user WHERE username=:username", nativeQuery = true)
    String findPasswordByUsername(@Param("username") String username);
}
