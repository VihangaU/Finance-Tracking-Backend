package financeTracking.app.repository;

import financeTracking.app.model.ERole;
import financeTracking.app.model.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(ERole name);
}
