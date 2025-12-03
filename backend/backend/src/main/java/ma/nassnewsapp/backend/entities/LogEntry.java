package ma.nassnewsapp.backend.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "logs")
public class LogEntry {

    @Id
    private String id;

    @Field("timestamp")
    private LocalDateTime timestamp;

    @Field("level")
    private String level; // ERROR, WARN, INFO, DEBUG

    @Field("service")
    private String service; // Service name (e.g., "Database", "UserService", "NewsService")

    @Field("message")
    private String message;

    @Field("exception")
    private String exception; // Stack trace or exception details

    @Field("userId")
    private String userId; // Optional: user ID if error is user-related

    @Field("requestPath")
    private String requestPath; // Optional: API endpoint where error occurred

    public LogEntry() {
        this.timestamp = LocalDateTime.now();
    }

    public LogEntry(String level, String service, String message) {
        this();
        this.level = level;
        this.service = service;
        this.message = message;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }
}

