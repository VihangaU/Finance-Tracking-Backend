package financeTracking.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "reports")
public class Report {
    @Id
    private String id;

    @DBRef
    private User user;

    private Date startDate;
    private Date endDate;
    private double totalIncome;
    private double totalExpenses;
    private List<Transactions> transactions;

    public Report(User user, Date startDate, Date endDate, double totalIncome, double totalExpenses, List<Transactions> transactions) {
        this.user = user;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.transactions = transactions;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public double getTotalIncome() { return totalIncome; }
    public void setTotalIncome(double totalIncome) { this.totalIncome = totalIncome; }

    public double getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(double totalExpenses) { this.totalExpenses = totalExpenses; }

    public List<Transactions> getTransactions() { return transactions; }
    public void setTransactions(List<Transactions> transactions) { this.transactions = transactions; }
}