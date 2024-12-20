package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.BloodPressureMeasurement;
import org.springframework.stereotype.Repository;

@Repository
public interface BloodPressureMeasurementRepository extends JpaRepository<BloodPressureMeasurement, Long> {}