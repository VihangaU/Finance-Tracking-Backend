package financeTracking.app.repository;

import financeTracking.app.model.Transactions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface TransactionRepository extends MongoRepository<Transactions, String> {
    List<Transactions> findByUserId(String userId);
    @Query("{ 'userId': ?0, 'tags': { $in: [?1] } }")
    List<Transactions> findByTag(String userId, String tag);

    @Query("{ 'userId': ?0, 'category': { $in: [?1] } }")
    List<Transactions> findByCategory(String userId, String category);

    List<Transactions> findByUserIdAndDateBetween(String userId, Date startDate, Date endDate);

}
