package financeTracking.app.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TransactionAddDTO {
    private String userId; // Reference to user
    private String type; // "income" or "expense"
    private double amount;
    private String category;
    private LocalDateTime date;
    private String description;
    private boolean recurring;
    private String interval;
    private LocalDateTime endDate;
    private List<String> tags;
    private String currency; // e.g., "USD", "LKR"
    private double exchangeRate;

    public TransactionAddDTO() {}

    public TransactionAddDTO(String userId, String type, double amount, String category, LocalDateTime date, String description, boolean recurring, String interval, LocalDateTime endDate, List<String> tags, String currency, double exchangeRate) {
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.recurring = recurring;
        this.interval = interval;
        this.endDate = endDate;
        this.tags = tags;
        this.currency = currency;
        this.exchangeRate = exchangeRate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}