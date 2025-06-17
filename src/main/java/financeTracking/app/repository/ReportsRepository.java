package financeTracking.app.repository;

import financeTracking.app.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportsRepository extends MongoRepository<Report, String> {
    List<Report> findByUserId(String userId);
}
