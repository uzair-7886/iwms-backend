package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.iwms.iwms.entity.Security;

@Repository
public interface SecurityRepository extends JpaRepository<Security, Long> {
}

