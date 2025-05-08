// src/main/java/MiniSocial/Model/NotificationEvent.java
package MiniSocial.Model;

import java.io.Serializable;
import java.util.Date;

public class NotificationEvent implements Serializable {
    // Event type constants
    public static final String FRIEND_REQUEST = "FRIEND_REQUEST";
    public static final String LIKE = "LIKE";
    public static final String COMMENT = "COMMENT";
    public static final String GROUP_JOIN = "GROUP_JOIN";
    public static final String GROUP_LEAVE = "GROUP_LEAVE";

    private String eventType;
    private String sourceUserId;
    private String targetUserId;
    private String postId;
    private String groupId;
    private Date timestamp;
    private String message;

    // Default constructor
    public NotificationEvent() {
        this.timestamp = new Date();
    }

    // Basic constructor
    public NotificationEvent(String eventType, String sourceUserId, String targetUserId) {
        this();
        this.eventType = eventType;
        this.sourceUserId = sourceUserId;
        this.targetUserId = targetUserId;
    }

    // Full constructor
    public NotificationEvent(String eventType, String sourceUserId, String targetUserId,
                             String postId, String groupId, String message) {
        this(eventType, sourceUserId, targetUserId);
        this.postId = postId;
        this.groupId = groupId;
        this.message = message;
    }

    // Getters and Setters
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(String sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // Utility methods
    public boolean isPostRelated() {
        return postId != null && !postId.isEmpty();
    }

    public boolean isGroupRelated() {
        return groupId != null && !groupId.isEmpty();
    }

    @Override
    public String toString() {
        return "NotificationEvent{" +
                "eventType='" + eventType + '\'' +
                ", sourceUserId='" + sourceUserId + '\'' +
                ", targetUserId='" + targetUserId + '\'' +
                ", postId='" + postId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", timestamp=" + timestamp +
                ", message='" + message + '\'' +
                '}';
    }

    // Builder pattern for fluent creation
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String eventType;
        private String sourceUserId;
        private String targetUserId;
        private String postId;
        private String groupId;
        private String message;

        public Builder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }

        public Builder sourceUserId(String sourceUserId) {
            this.sourceUserId = sourceUserId;
            return this;
        }

        public Builder targetUserId(String targetUserId) {
            this.targetUserId = targetUserId;
            return this;
        }

        public Builder postId(String postId) {
            this.postId = postId;
            return this;
        }

        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public NotificationEvent build() {
            NotificationEvent event = new NotificationEvent();
            event.setEventType(eventType);
            event.setSourceUserId(sourceUserId);
            event.setTargetUserId(targetUserId);
            event.setPostId(postId);
            event.setGroupId(groupId);
            event.setMessage(message);
            return event;
        }
    }
}