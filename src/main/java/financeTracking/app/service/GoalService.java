package financeTracking.app.service;

import financeTracking.app.model.Goal;
import financeTracking.app.model.User;
import financeTracking.app.repository.GoalRepository;
import financeTracking.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {
    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    @Autowired
    public GoalService(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    public Goal createGoal(String userId, Goal goal) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        goal.setUser(user);
        goal.setProgressPercentage(goal.calculateProgress());
        return goalRepository.save(goal);
    }

    public List<Goal> getGoalsByUser(String userId) {
        return goalRepository.findByUserId(userId);
    }

    public Goal updateGoal(String goalId, double amount) {
        Optional<Goal> goalOptional = goalRepository.findById(goalId);

        if (goalOptional.isEmpty()) {
            throw new RuntimeException("Goal not found");
        }

        Goal goal = goalOptional.get();
        goal.setCurrentAmount(goal.getCurrentAmount() + amount);
        goal.setProgressPercentage(goal.calculateProgress());

        return goalRepository.save(goal);
    }

    public void deleteGoal(String goalId) {
        goalRepository.deleteById(goalId);
    }
}
