package com.example.iwms.iwms.repository;
import com.example.iwms.iwms.entity.TemperatureMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.iwms.iwms.entity.User;
import java.util.List;


@Repository
public interface TemperatureMeasurementRepository extends JpaRepository<TemperatureMeasurement, Long> {

    List<TemperatureMeasurement> findTop7ByUserOrderByTimestampAsc(User user);
}