package financeTracking.app.controller;

import financeTracking.app.dto.TransactionUpdateDTO;
import financeTracking.app.model.Budget;
import financeTracking.app.model.Goal;
import financeTracking.app.model.Transactions;
import financeTracking.app.model.User;
import financeTracking.app.service.BudgetService;
import financeTracking.app.service.GoalService;
import financeTracking.app.service.TransactionService;
import financeTracking.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/put")
public class PutContoller {
    private static final Logger logger = LoggerFactory.getLogger(PutContoller.class);

    private final TransactionService transactionService;
    private final UserService userService;
    private final BudgetService budgetService;
    private final GoalService goalService;

    @Autowired
    public PutContoller(TransactionService transactionService, UserService userService, BudgetService budgetService, GoalService goalService) {
        this.transactionService = transactionService;
        this.userService = userService;
        this.budgetService = budgetService;
        this.goalService = goalService;
    }

    @PutMapping("/transactions/user/{userId}/transaction/{transactionId}")
    public ResponseEntity<?> updateTransaction(
            @PathVariable String userId,
            @PathVariable String transactionId,
            @RequestBody TransactionUpdateDTO transactionUpdateDTO) {

        logger.info("Received request to update transaction with ID: {} for user: {}", transactionId, userId);

        // Check if user exists
        Optional<User> userOptional = userService.getUserById(userId);
        if (!userOptional.isPresent()) {
            // If user doesn't exist, return 404 Not Found with a helpful message
            logger.warn("User not found with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            Transactions updatedTransaction = transactionService.updateTransaction(userId, transactionId, transactionUpdateDTO);
            logger.info("Successfully updated transaction for user: {} with transaction ID: {}", userId, transactionId);
            return ResponseEntity.ok(updatedTransaction);
        } catch (RuntimeException e) {
            logger.error("Error updating transaction for user {} with transaction ID {}: {}", userId, transactionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error updating transaction for user {} with transaction ID {}: {}", userId, transactionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/user/{userId}/transaction/{transactionId}")
    public ResponseEntity<?> deleteTransaction(
            @PathVariable String userId,
            @PathVariable String transactionId) {

        logger.info("Received request to delete transaction with ID: {} for user: {}", transactionId, userId);

        // Check if user exists
        Optional<User> userOptional = userService.getUserById(userId);
        if (!userOptional.isPresent()) {
            // If user doesn't exist, return 404 Not Found with a helpful message
            logger.warn("User not found with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        try {
            transactionService.deleteTransaction(userId, transactionId);
            logger.info("Successfully deleted transaction with ID: {} for user: {}", transactionId, userId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Transaction deleted successfully");
        } catch (RuntimeException e) {
            logger.error("Error deleting transaction for user {} with transaction ID {}: {}", userId, transactionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting transaction for user {} with transaction ID {}: {}", userId, transactionId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @PutMapping("/budget/{budgetId}")
    public ResponseEntity<Budget> updateBudget(@PathVariable String budgetId, @RequestBody Budget budget) {
        logger.info("Received request to update budget with ID: {}", budgetId);

        try {
            Budget updatedBudget = budgetService.updateBudget(budgetId, budget);
            logger.info("Successfully updated budget with ID: {}", budgetId);
            return ResponseEntity.ok(updatedBudget);
        } catch (Exception e) {
            logger.error("Error updating budget with ID {}: {}", budgetId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete/budget/{budgetId}")
    public ResponseEntity<Void> deleteBudget(@PathVariable String budgetId) {
        logger.info("Received request to delete budget with ID: {}", budgetId);

        try {
            budgetService.deleteBudget(budgetId);
            logger.info("Successfully deleted budget with ID: {}", budgetId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting budget with ID {}: {}", budgetId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/goal/{goalId}")
    public ResponseEntity<Goal> updateGoal(@PathVariable String goalId, @RequestParam double amount) {
        logger.info("Received request to update goal with ID: {}. New amount: {}", goalId, amount);

        try {
            Goal updatedGoal = goalService.updateGoal(goalId, amount);
            logger.info("Successfully updated goal with ID: {}", goalId);
            return ResponseEntity.ok(updatedGoal);
        } catch (Exception e) {
            logger.error("Error updating goal with ID {}: {}", goalId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/delete/goal/{goalId}")
    public ResponseEntity<String> deleteGoal(@PathVariable String goalId) {
        logger.info("Received request to delete goal with ID: {}", goalId);

        try {
            goalService.deleteGoal(goalId);
            logger.info("Successfully deleted goal with ID: {}", goalId);
            return ResponseEntity.ok("Goal deleted successfully");
        } catch (Exception e) {
            logger.error("Error deleting goal with ID {}: {}", goalId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }
}
