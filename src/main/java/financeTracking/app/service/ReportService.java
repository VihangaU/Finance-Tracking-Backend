package financeTracking.app.service;

import financeTracking.app.model.Report;
import financeTracking.app.model.Transactions;
import financeTracking.app.model.User;
import financeTracking.app.repository.ReportsRepository;
import financeTracking.app.repository.TransactionRepository;
import financeTracking.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final ReportsRepository reportsRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public ReportService(ReportsRepository reportsRepository, TransactionRepository transactionRepository, UserRepository userRepository) {
        this.reportsRepository = reportsRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    public Report generateReport(String userId, Date startDate, Date endDate, List<String> categories, List<String> tags) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        User user = userOptional.get();

        List<Transactions> transactions = transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate);

        double totalIncome = transactions.stream()
                .filter(t -> "income".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transactions::getConvertedAmount) // Use converted amount
                .sum();

        double totalExpenses = transactions.stream()
                .filter(t -> "expense".equalsIgnoreCase(t.getType()))
                .mapToDouble(Transactions::getConvertedAmount)
                .sum();

        Report report = new Report(user, startDate, endDate, totalIncome, totalExpenses, transactions);
        return reportsRepository.save(report);
    }

    public List<Report> getUserReports(String userId) {
        return reportsRepository.findByUserId(userId);
    }
}
