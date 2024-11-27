package com.example.iwms.iwms.repository;

import com.example.iwms.iwms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Additional query methods can be defined here
}