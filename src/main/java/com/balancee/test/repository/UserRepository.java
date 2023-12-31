package com.balancee.test.repository;

import com.balancee.test.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    //find By username query
    Optional<User> findByUsername(String userName);
}
