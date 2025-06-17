package financeTracking.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "transactions")
public class Transactions {
    @Id
    private String id;
    private String type; // "income" or "expense"
    private double amount;
    private String category;
    private LocalDateTime date;
    private String description;
    private Recurring recurring;
    private List<String> tags;
    private String currency; // e.g., "USD", "EUR"
    private double convertedAmount; // Amount converted to user's default currency
    private double exchangeRate; // Rate at the time of transaction

    @DBRef
    private User user; // Reference to user

    public Transactions() {}

    public Transactions(String type, double amount, String category, LocalDateTime date, String description, Recurring recurring, List<String> tags, String currency, double convertedAmount, double exchangeRate, User user) {
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
        this.recurring = recurring;
        this.tags = tags;
        this.currency = currency;
        this.convertedAmount = convertedAmount;
        this.exchangeRate = exchangeRate;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Recurring getRecurring() {
        return recurring;
    }

    public void setRecurring(Recurring recurring) {
        this.recurring = recurring;
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

    public double getConvertedAmount() {
        return convertedAmount;
    }

    public void setConvertedAmount(double convertedAmount) {
        this.convertedAmount = convertedAmount;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}