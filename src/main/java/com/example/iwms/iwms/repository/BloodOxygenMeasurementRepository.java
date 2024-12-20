package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.BloodOxygenMeasurement;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodOxygenMeasurementRepository extends JpaRepository<BloodOxygenMeasurement, Long> {}
