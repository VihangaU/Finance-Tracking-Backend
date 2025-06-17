package financeTracking.app.repository;

import financeTracking.app.model.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {
    List<Budget> findByUserId(String userId);
    Optional<Budget> findByUserAndCategory(String userId, String category);
}
