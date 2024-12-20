package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.HeartRateMeasurement;
import org.springframework.stereotype.Repository;

@Repository
public interface HeartRateMeasurementRepository extends JpaRepository<HeartRateMeasurement, Long> {}