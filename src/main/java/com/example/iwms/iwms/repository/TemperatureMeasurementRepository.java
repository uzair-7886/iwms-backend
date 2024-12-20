package com.example.iwms.iwms.repository;
import com.example.iwms.iwms.entity.TemperatureMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TemperatureMeasurementRepository extends JpaRepository<TemperatureMeasurement, Long> {}