package financeTracking.app.model;

import java.time.LocalDateTime;

public class Recurring {
    private boolean isRecurring;
    private String interval;
    private LocalDateTime endDate;

    public Recurring() {
    }

    public Recurring(boolean isRecurring, String interval, LocalDateTime endDate) {
        this.isRecurring = isRecurring;
        this.interval = interval;
        this.endDate = endDate;
    }

    public boolean isRecurring() {
        return isRecurring;
    }

    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
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
}
