package financeTracking.app.controller;

import financeTracking.app.dto.TransactionAddDTO;
import financeTracking.app.model.*;
import financeTracking.app.repository.GoalRepository;
import financeTracking.app.repository.UserRepository;
import financeTracking.app.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(value = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/post")
public class PostController {
    private static final Logger logger = LoggerFactory.getLogger(PostController.class);

    private final UserService userService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final UserRepository userRepository;
    private final GoalService goalService;
    private final ReportService reportService;

    @Autowired
    public PostController(UserService userService, TransactionService transactionService, BudgetService budgetService, UserRepository userRepository, GoalService goalService, ReportService reportService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.userRepository = userRepository;
        this.goalService = goalService;
        this.reportService = reportService;
    }

    @PostMapping("/transactions")
    public ResponseEntity<?> addTransaction(@RequestBody TransactionAddDTO transactionAddDTO) {
        try {
            // Check if user exists
            Optional<User> userOptional = userRepository.findById(transactionAddDTO.getUserId());
            if (!userOptional.isPresent()) {
                logger.warn("User not found for transaction with ID: {}", transactionAddDTO.getUserId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            logger.info("Adding transaction for user: {}", transactionAddDTO.getUserId());
            Transactions newTransaction = transactionService.saveTransaction(transactionAddDTO);
            logger.info("Successfully added transaction for user: {}", transactionAddDTO.getUserId());

            return ResponseEntity.status(HttpStatus.CREATED).body(newTransaction);
        } catch (RuntimeException e) {
            logger.error("Error adding transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error adding transaction: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/users")
    public ResponseEntity<?> addUsers(@RequestBody User user) {
        try {
            logger.info("Adding new user with email: {}", user.getEmail());
            User newUser = userService.saveUser(user);
            logger.info("Successfully added user: {}", newUser.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (RuntimeException e) {
            logger.error("Error adding user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error adding user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error: " + e.getMessage());
        }
    }

    @PostMapping("/budgets/{userId}")
    public ResponseEntity<?> createBudget(@PathVariable String userId, @RequestBody Budget budget) {
        try {
            // Check if user exists
            Optional<User> userOptional = userRepository.findById(userId);
            if (!userOptional.isPresent()) {
                logger.warn("User not found for budget creation with ID: {}", userId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            logger.info("Creating budget for user: {}", userId);
            Budget savedBudget = budgetService.addBudget(userId, budget);
            logger.info("Successfully created budget for user: {}", userId);

            return ResponseEntity.ok(savedBudget);
        } catch (Exception e) {
            logger.error("Error creating budget for user {}: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/send/{userId}")
    public ResponseEntity<?> sendNotification(@PathVariable String userId, @RequestParam String subject, @RequestParam String message) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            logger.warn("User not found for notification with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOptional.get();
        logger.info("Sending notification to user: {}", userId);
        transactionService.sendNotification(user, subject, message);
        logger.info("Notification sent to user: {}", user.getEmail());

        return ResponseEntity.ok("Notification sent to " + user.getEmail());
    }

    @PostMapping("/goals/{userId}")
    public ResponseEntity<Goal> createGoal(@PathVariable String userId, @RequestBody Goal goal) {Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            logger.warn("User not found for goal creation with ID: {}", userId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        logger.info("Creating goal for user: {}", userId);
        Goal createdGoal = goalService.createGoal(userId, goal);
        logger.info("Successfully created goal for user: {}", userId);

        return ResponseEntity.ok(createdGoal);
    }

    @PostMapping("/generate/report")
    public Report generateReport(@RequestParam String userId,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                 @RequestParam(required = false) List<String> categories,
                                 @RequestParam(required = false) List<String> tags) {
        logger.info("Generating report for user: {}", userId);
        return reportService.generateReport(userId, startDate, endDate, categories, tags);
    }
}
