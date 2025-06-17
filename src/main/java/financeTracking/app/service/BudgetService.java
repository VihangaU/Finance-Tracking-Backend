package financeTracking.app.service;

import financeTracking.app.model.Budget;
import financeTracking.app.model.User;
import financeTracking.app.repository.BudgetRepository;
import financeTracking.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private UserRepository userRepository;

    public Budget addBudget(String userId, Budget budget) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found with ID: " +userId);
        }

        User user = userOptional.get();
        budget.setUser(user);
        return budgetRepository.save(budget);
    }

    public List<Budget> getUserBudget(String userId) {
        return budgetRepository.findByUserId(userId);
    }

    public Optional<Budget> getBudgetByCategory(String userId, String category) {
        return budgetRepository.findByUserAndCategory(userId, category).stream().findFirst();
    }

    public Budget updateBudget(String budgetId, Budget updatedBudget) {
        return budgetRepository.findById(budgetId).map(existingBudget -> {
            existingBudget.setCategory(updatedBudget.getCategory());
            existingBudget.setAmount(updatedBudget.getAmount());
            existingBudget.setPeriod(updatedBudget.getPeriod());
            existingBudget.setNotificationsEnabled(updatedBudget.isNotificationsEnabled());
            return budgetRepository.save(existingBudget);
        }).orElseThrow(() -> new RuntimeException("Budget not found"));
    }

    public void deleteBudget(String budgetId) {
        budgetRepository.deleteById(budgetId);
    }
}
