package financeTracking.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "budgets")
public class Budget {
    @Id
    private String id;
    private String category;
    private double amount;
    private String period;
    private boolean notificationsEnabled;
    private LocalDateTime createdAt;
    @DBRef
    private User user;

    public Budget() {
        this.createdAt = LocalDateTime.now();
    }

    public Budget(String id, String category, double amount, String period, boolean notificationsEnabled, LocalDateTime createdAt, User user) {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.period = period;
        this.notificationsEnabled = notificationsEnabled;
        this.createdAt = createdAt;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
