package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.BloodPressureMeasurement;
import org.springframework.stereotype.Repository;
import com.example.iwms.iwms.entity.User;
import java.util.List;

@Repository
public interface BloodPressureMeasurementRepository extends JpaRepository<BloodPressureMeasurement, Long> {
    List<BloodPressureMeasurement> findTop7ByUserOrderByTimestampAsc(User user);
}