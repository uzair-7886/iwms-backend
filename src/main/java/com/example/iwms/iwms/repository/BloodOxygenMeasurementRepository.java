package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.BloodOxygenMeasurement;
import org.springframework.stereotype.Repository;
import com.example.iwms.iwms.entity.User;
import java.util.List;

@Repository
public interface BloodOxygenMeasurementRepository extends JpaRepository<BloodOxygenMeasurement, Long> {
    List<BloodOxygenMeasurement> findTop7ByUserOrderByTimestampAsc(User user);
}
