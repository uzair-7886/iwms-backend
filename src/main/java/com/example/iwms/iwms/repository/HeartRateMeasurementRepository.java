package com.example.iwms.iwms.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.iwms.iwms.entity.HeartRateMeasurement;
import org.springframework.stereotype.Repository;
import com.example.iwms.iwms.entity.User;
import java.util.List;

@Repository
public interface HeartRateMeasurementRepository extends JpaRepository<HeartRateMeasurement, Long> {

    List<HeartRateMeasurement> findTop7ByUserOrderByTimestampAsc(User user);
}