package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.WeightMeasurement;
import org.springframework.stereotype.Repository;

@Repository
public interface WeightMeasurementRepository extends JpaRepository<WeightMeasurement, Long> {

}

