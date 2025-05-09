package MiniSocial.Model;

import java.io.Serializable;

public class NotificationEvent implements Serializable {
    private String eventName;
    private String recipientEmail;
    private String description;
    private String timestamp;

    // Constructor
    public NotificationEvent(String eventName, String recipientEmail, String description, String timestamp) {
        this.eventName = eventName;
        this.recipientEmail = recipientEmail;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "{" +
                "\"eventName\":\"" + eventName + "\"," +
                "\"recipientEmail\":\"" + recipientEmail + "\"," +
                "\"description\":\"" + description + "\"," +
                "\"timestamp\":\"" + timestamp + "\"" +
                "}";
    }
}
