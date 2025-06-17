package financeTracking.app.service;

import financeTracking.app.model.User;
import financeTracking.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User saveUser(User user) {
        // Check if email already exists
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("User with username " + user.getUsername() + " already exists.");
        }

        user.setId(null);

        // Save and return user
        return userRepository.save(user);
    }

    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    public boolean userExists(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.isPresent();
    }
}
