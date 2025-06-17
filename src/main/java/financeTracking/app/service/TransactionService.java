package financeTracking.app.service;

import financeTracking.app.dto.TransactionAddDTO;
import financeTracking.app.dto.TransactionUpdateDTO;
import financeTracking.app.model.*;
import financeTracking.app.repository.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    @Autowired
    private JavaMailSender mailSender;

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final ReportsRepository reportRepository;
    private final CurrencyService currencyService;

    @Autowired
    public TransactionService(TransactionRepository transactionRepository, UserRepository userRepository,
                              BudgetRepository budgetRepository, GoalRepository goalRepository,
                              ReportsRepository reportRepository, CurrencyService currencyService) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;
        this.reportRepository = reportRepository;
        this.currencyService = currencyService;
    }

    public List<Transactions> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transactions> getAllTransactionsByUser(String userId) {
        return transactionRepository.findByUserId(userId);
    }

    public List<Transactions> getAllTransactionsByTag(String userId, String tag) {
        return transactionRepository.findByTag(userId, tag);
    }

    public Transactions saveTransaction(TransactionAddDTO transactionAddDTO) {
        Optional<User> userOptional = userRepository.findById(transactionAddDTO.getUserId());

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();
        String transactionCurrency = transactionAddDTO.getCurrency();
        double convertedAmount = transactionAddDTO.getAmount();
        double exchangeRate = 1.0;

        // Convert if transaction currency is different from user's default currency
        if (!transactionCurrency.equals(user.getCurrency())) {
            exchangeRate = currencyService.getExchangeRate(transactionCurrency, user.getCurrency());
            convertedAmount = transactionAddDTO.getAmount() * exchangeRate;
        }

        Recurring recurring = transactionAddDTO.isRecurring() ? new Recurring(transactionAddDTO.isRecurring(), transactionAddDTO.getInterval(), transactionAddDTO.getEndDate()) : null;

        Transactions transactions = new Transactions(
                transactionAddDTO.getType(),
                transactionAddDTO.getAmount(),
                transactionAddDTO.getCategory(),
                transactionAddDTO.getDate(),
                transactionAddDTO.getDescription(),
                recurring,
                transactionAddDTO.getTags(),
                transactionAddDTO.getCurrency(),
                convertedAmount,
                exchangeRate,
                user
        );

        return transactionRepository.save(transactions);
    }

    public Transactions updateTransaction(String userId, String transactionId, TransactionUpdateDTO transactionUpdateDTO) {
        Optional<Transactions> existingTransactionOptional = transactionRepository.findById(transactionId);

        if (existingTransactionOptional.isEmpty()) {
            throw new RuntimeException("Transaction with ID " + transactionId + " not found");
        }

        Transactions existingTransaction = existingTransactionOptional.get();

        // Ensure the transaction belongs to the requesting user
        if (!existingTransaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: This transaction does not belong to the user.");
        }

        // Update transaction fields
        existingTransaction.setType(transactionUpdateDTO.getType());
        existingTransaction.setAmount(transactionUpdateDTO.getAmount());
        existingTransaction.setCategory(transactionUpdateDTO.getCategory());
        existingTransaction.setDate(transactionUpdateDTO.getDate());
        existingTransaction.setDescription(transactionUpdateDTO.getDescription());
        existingTransaction.setTags(transactionUpdateDTO.getTags());

        // Handle currency conversion if currency is updated
        if (!transactionUpdateDTO.getCurrency().equals(existingTransaction.getCurrency())) {
            double exchangeRate = currencyService.getExchangeRate(transactionUpdateDTO.getCurrency(), existingTransaction.getUser().getCurrency());
            double convertedAmount = transactionUpdateDTO.getAmount() * exchangeRate;

            existingTransaction.setCurrency(transactionUpdateDTO.getCurrency());
            existingTransaction.setExchangeRate(exchangeRate);
            existingTransaction.setConvertedAmount(convertedAmount);
        }

        // Handle recurring transaction updates
        if (transactionUpdateDTO.isRecurring() && transactionUpdateDTO.getEndDate() != null) {
            existingTransaction.setRecurring(new Recurring(transactionUpdateDTO.isRecurring(), transactionUpdateDTO.getInterval(), transactionUpdateDTO.getEndDate()));
        } else {
            existingTransaction.setRecurring(null);
        }

        return transactionRepository.save(existingTransaction);
    }

    public void deleteTransaction(String userId, String transactionId) {
        Optional<Transactions> transactionOptional = transactionRepository.findById(transactionId);

        if (transactionOptional.isEmpty()) {
            throw new RuntimeException("Transaction with ID " + transactionId + " not found");
        }

        Transactions transaction = transactionOptional.get();

        // Ensure the transaction belongs to the requesting user
        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: This transaction does not belong to the user.");
        }

        // Delete the transaction
        transactionRepository.delete(transaction);
    }
    
    private void allocateToGoals(String userId, double incomeAmount) {
        List<Goal> goals = goalRepository.findByUserId(userId);

        for (Goal goal : goals) {
            if (goal.isAutoSaveEnabled()) {
                double allocation = incomeAmount * 0.1;
                goal.setCurrentAmount(goal.getCurrentAmount() + allocation);
                goal.setProgressPercentage(goal.calculateProgress());
                goalRepository.save(goal);
            }
        }
    }

    private void checkBudgetExceedance(User user, String category) {
        Optional<Budget> budgetOptional = budgetRepository.findByUserAndCategory(user.getId(), category);

        if (budgetOptional.isPresent()) {
            Budget budget = budgetOptional.get();

            double totalSpent = transactionRepository.findByCategory(user.getId(), category)
                    .stream().mapToDouble(Transactions::getConvertedAmount).sum();

            double budgetLimit = budget.getAmount();

            if (totalSpent > budgetLimit) {
                sendNotification(user, "Budget Exceeded!",
                        "You have exceeded your budget for " + category + "! Total spent: " + totalSpent + "/" + budgetLimit);
            } else if (totalSpent >= budgetLimit * 0.9) {
                sendNotification(user, "Budget Warning",
                        "You are close to exceeding your budget for " + category + ". Total spent: " + totalSpent + "/" + budgetLimit);
            }
        }
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void checkRecurringTransactions() {
        LocalDate today = LocalDate.now();

        List<Transactions> recurringTransactions = transactionRepository.findAll().stream()
                .filter(t -> t.getRecurring() != null && t.getRecurring().isRecurring())
                .collect(Collectors.toList());

        for (Transactions transaction : recurringTransactions) {
            LocalDate transactionDate = transaction.getDate().toLocalDate();

            if (transactionDate.isBefore(today)) {
                sendNotification(transaction.getUser(), "Missed Recurring Transaction!",
                        "You missed a recurring transaction: " + transaction.getDescription());
            }

            if (transactionDate.equals(today)) {
                sendNotification(transaction.getUser(), "Upcoming Transaction Today!",
                        "You have a recurring transaction today: " + transaction.getDescription());
            }
        }
    }

    @Scheduled(cron = "0 0 10 * * ?")
    public void checkBudgetLimits() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            List<Budget> budgets = budgetRepository.findByUserId(user.getId());

            for (Budget budget : budgets) {
                checkBudgetExceedance(user, budget.getCategory());
            }
        }
    }

    public void sendNotification(User user, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(message, true);
            mailSender.send(mimeMessage);
            System.out.println("✅ Email sent successfully to: " + user.getEmail());
        } catch (Exception e) {
            System.err.println("❌ Error sending email: " + e.getMessage());
        }
    }

    public Report generateReport(String userId, Date startDate, Date endDate) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOptional.get();

        List<Transactions> transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        double totalIncome = transactions.stream()
                .filter(t -> "income".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transactions::getConvertedAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transactions::getConvertedAmount)
                .sum();

        Report report = new Report(user, startDate, endDate, totalIncome, totalExpenses, transactions);
        return reportRepository.save(report);
    }
}
