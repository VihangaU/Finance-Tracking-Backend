package financeTracking.app.controller;

import financeTracking.app.exception.ResourceNotFoundException;
import financeTracking.app.model.*;
import financeTracking.app.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/get")
public class GetController {
    private static final Logger logger = LoggerFactory.getLogger(GetController.class);

    private final UserService userService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final GoalService goalService;
    private final ReportService reportService;

    @Autowired
    public GetController(UserService userService, TransactionService transactionService, BudgetService budgetService, GoalService goalService, ReportService reportService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.goalService = goalService;
        this.reportService = reportService;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        logger.info("Received request to get all users.");
        try {
            List<User> users = userService.getAllUsers();
            logger.info("Successfully retrieved {} users.", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            logger.error("Failed to fetch users: ", e);
            throw new ResourceNotFoundException("Users not found");
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<Transactions>> getAllTransactions() {
        logger.info("Received request to get all transactions.");
        try {
            List<Transactions> transactions = transactionService.getAllTransactions();
            logger.info("Successfully retrieved {} transactions.", transactions.size());
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Failed to fetch transactions: ", e);
            throw new ResourceNotFoundException("Transactions not found");
        }
    }

    @GetMapping("/transactions/user/{userId}")
    public ResponseEntity<List<Transactions>> getAllTransactionsByUser(@PathVariable String userId) {
        logger.info("Received request to get transactions for user: {}", userId);
        try {
            // Check if the user exists
            Optional<User> userOptional = userService.getUserById(userId);
            if (!userOptional.isPresent()) {
                // If user doesn't exist, return 404 Not Found with a helpful message
                logger.warn("User not found with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // If user exists, fetch transactions
            List<Transactions> transactions = transactionService.getAllTransactionsByUser(userId);
            if (transactions.isEmpty()) {
                logger.warn("No transactions found for user: {}", userId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();  // 204 No Content
            } else {
                logger.info("Successfully retrieved {} transactions for user: {}", transactions.size(), userId);
                return ResponseEntity.ok(transactions);  // 200 OK
            }
        } catch (Exception e) {
            logger.error("Error fetching transactions for user {}: {}", userId, e.getMessage());
            throw new ResourceNotFoundException("Transactions for user " + userId + " not found");
        }
    }

    @GetMapping("/transactions/user/{userId}/tag/{tag}")
    public ResponseEntity<List<Transactions>> getAllTransactionsByTag(@PathVariable String userId, @PathVariable String tag) {
        logger.info("Received request to get transactions for user: {} with tag: {}", userId, tag);
        try {
            Optional<User> userOptional = userService.getUserById(userId);
            if (!userOptional.isPresent()) {
                // If user doesn't exist, return 404 Not Found with a helpful message
                logger.warn("User not found with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<Transactions> transactions = transactionService.getAllTransactionsByTag(userId, tag);
            if (transactions.isEmpty()) {
                logger.warn("No transactions found for user: {} with tag: {}", userId, tag);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                logger.info("Successfully retrieved {} transactions for user: {} with tag: {}", transactions.size(), userId, tag);
                return ResponseEntity.ok(transactions);
            }
//            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            logger.error("Error fetching transactions for user {} with tag {}: {}", userId, tag, e.getMessage());
            throw new ResourceNotFoundException("Transactions for user " + userId + " with tag " + tag + " not found");
        }
    }

    @GetMapping("/budgets/{userId}")
    public ResponseEntity<List<Budget>> getBudgetByUser(@PathVariable String userId) {
        logger.info("Received request to get budget for user: {}", userId);
        try {
            Optional<User> userOptional = userService.getUserById(userId);
            if (!userOptional.isPresent()) {
                // If user doesn't exist, return 404 Not Found with a helpful message
                logger.warn("User not found with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            List<Budget> budget = budgetService.getUserBudget(userId);
            if (budget.isEmpty()) {
                logger.warn("No budget found for user: {}", userId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            } else {
                logger.info("Successfully retrieved budget for user: {}", userId);
                return ResponseEntity.ok(budget);
            }
//            return ResponseEntity.ok(budget);
        } catch (Exception e) {
            logger.error("Error fetching budget for user {}: {}", userId, e.getMessage());
            throw new ResourceNotFoundException("Budget for user " + userId + " not found");
        }
    }

    @GetMapping("/budgets/{userId}/category/{category}")
    public ResponseEntity<Budget> getBudgetByCategory(@PathVariable String userId, @PathVariable String category) {
        logger.info("Received request to get budget category {} for user: {}", category, userId);
        Optional<Budget> budget = budgetService.getBudgetByCategory(userId, category);
        if (budget.isPresent()) {
            logger.info("Successfully retrieved budget for category: {} and user: {}", category, userId);
            return ResponseEntity.ok(budget.get());
        } else {
            logger.warn("Budget category {} not found for user {}", category, userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/goals/{userId}")
    public ResponseEntity<List<Goal>> getGoalsByUser(@PathVariable String userId) {
        logger.info("Received request to get goals for user: {}", userId);
        return ResponseEntity.ok(goalService.getGoalsByUser(userId));
    }

    @GetMapping("/reports/{userId}")
    public ResponseEntity<List<Report>> getUserReports(@PathVariable String userId) {
        logger.info("Received request to get reports for user: {}", userId);
        List<Report> reports = reportService.getUserReports(userId);
        if (reports.isEmpty()) {
            logger.warn("No reports found for user: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        logger.info("Successfully retrieved {} reports for user: {}", reports.size(), userId);
        return ResponseEntity.ok(reports);
    }
}
