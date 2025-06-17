package financeTracking.app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "goals")
public class Goal {
    @Id
    private String id;
    private String goalName;
    private double targetAmount;
    private double currentAmount;
    private LocalDate deadline;
    private boolean autoSaveEnabled;
    private double progressPercentage;

    @DBRef
    private User user;

    public Goal() {
    }

    public Goal(String id, String goalName, double targetAmount, double currentAmount, LocalDate deadline, boolean autoSaveEnabled, double progressPercentage, User user) {
        this.id = id;
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.deadline = deadline;
        this.autoSaveEnabled = autoSaveEnabled;
        this.progressPercentage = progressPercentage;
        this.user = user;
    }

    public double calculateProgress() {
        return (currentAmount/targetAmount)*100;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(double currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public boolean isAutoSaveEnabled() {
        return autoSaveEnabled;
    }

    public void setAutoSaveEnabled(boolean autoSaveEnabled) {
        this.autoSaveEnabled = autoSaveEnabled;
    }

    public double getProgressPercentage() {
        return progressPercentage;
    }

    public void setProgressPercentage(double progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
