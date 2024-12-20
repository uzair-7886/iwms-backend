package com.example.iwms.iwms.repository;
import com.example.iwms.iwms.entity.LatestHealthMetrics;
import com.example.iwms.iwms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LatestHealthMetricsRepository extends JpaRepository<LatestHealthMetrics, Long> {
    Optional<LatestHealthMetrics> findByUser(User user);
}
