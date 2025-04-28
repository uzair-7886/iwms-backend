package com.example.iwms.iwms.repository;

import com.example.iwms.iwms.entity.LatestHealthMetrics;
import com.example.iwms.iwms.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;
import com.example.iwms.iwms.entity.User;


@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
      Optional<Recommendation> findByMetric(LatestHealthMetrics metric);
      List<Recommendation> findByUser(User user);
}
