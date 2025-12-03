package ma.nassnewsapp.backend.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "error_logs")
public class ErrorLog {

    @Id
    private String id;

    @Field("timestamp")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    @Field("level")
    private String level; // ERROR, WARN, INFO, DEBUG

    @Field("service")
    private String service; // Service/component name

    @Field("message")
    private String message;

    @Field("exception")
    private String exception; // Exception class name

    @Field("stackTrace")
    private String stackTrace; // Full stack trace

    @Field("userId")
    private String userId; // Optional: user who triggered the error

    @Field("requestPath")
    private String requestPath; // Optional: API endpoint

    @Field("requestMethod")
    private String requestMethod; // GET, POST, etc.

    public ErrorLog() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorLog(String level, String service, String message) {
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

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
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

    public String getRequestMethod() {
        return requestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }
}

