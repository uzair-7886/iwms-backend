package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.GlucoseMeasurement;
import org.springframework.stereotype.Repository;

@Repository
public interface GlucoseMeasurementRepository extends JpaRepository<GlucoseMeasurement, Long> {}
