package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.WeightMeasurement;
import org.springframework.stereotype.Repository;
import com.example.iwms.iwms.entity.User;
import java.util.List;

@Repository
public interface WeightMeasurementRepository extends JpaRepository<WeightMeasurement, Long> {
    List<WeightMeasurement> findTop7ByUserOrderByTimestampAsc(User user);
}

