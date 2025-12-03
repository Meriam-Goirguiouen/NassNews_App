package ma.nassnewsapp.backend.services;

import ma.nassnewsapp.backend.entities.ErrorLog;
import ma.nassnewsapp.backend.repositories.ErrorLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ErrorLogService {

    private static final Logger logger = LoggerFactory.getLogger(ErrorLogService.class);

    private final ErrorLogRepository errorLogRepository;

    @Autowired
    public ErrorLogService(ErrorLogRepository errorLogRepository) {
        this.errorLogRepository = errorLogRepository;
        logger.info("ErrorLogService initialized");
    }

    /**
     * Log an error with exception details
     */
    public ErrorLog logError(String service, String message, Exception exception) {
        ErrorLog errorLog = new ErrorLog("ERROR", service, message);
        
        if (exception != null) {
            errorLog.setException(exception.getClass().getName());
            errorLog.setStackTrace(getStackTrace(exception));
        }
        
        ErrorLog saved = errorLogRepository.save(errorLog);
        logger.debug("Error logged: {} - {}", service, message);
        return saved;
    }

    /**
     * Log a warning
     */
    public ErrorLog logWarning(String service, String message) {
        ErrorLog errorLog = new ErrorLog("WARN", service, message);
        ErrorLog saved = errorLogRepository.save(errorLog);
        logger.debug("Warning logged: {} - {}", service, message);
        return saved;
    }

    /**
     * Log an info message
     */
    public ErrorLog logInfo(String service, String message) {
        ErrorLog errorLog = new ErrorLog("INFO", service, message);
        ErrorLog saved = errorLogRepository.save(errorLog);
        logger.debug("Info logged: {} - {}", service, message);
        return saved;
    }

    /**
     * Log with full context (for API requests)
     */
    public ErrorLog logErrorWithContext(String service, String message, Exception exception, 
                                       String userId, String requestPath, String requestMethod) {
        ErrorLog errorLog = new ErrorLog("ERROR", service, message);
        
        if (exception != null) {
            errorLog.setException(exception.getClass().getName());
            errorLog.setStackTrace(getStackTrace(exception));
        }
        
        errorLog.setUserId(userId);
        errorLog.setRequestPath(requestPath);
        errorLog.setRequestMethod(requestMethod);
        
        ErrorLog saved = errorLogRepository.save(errorLog);
        logger.debug("Error logged with context: {} - {}", service, message);
        return saved;
    }

    /**
     * Get all error logs, ordered by timestamp (newest first)
     */
    public List<ErrorLog> getAllLogs() {
        return errorLogRepository.findAllByOrderByTimestampDesc();
    }

    /**
     * Get logs by level
     */
    public List<ErrorLog> getLogsByLevel(String level) {
        return errorLogRepository.findByLevel(level);
    }

    /**
     * Get logs by service
     */
    public List<ErrorLog> getLogsByService(String service) {
        return errorLogRepository.findByService(service);
    }

    /**
     * Get logs within a time range
     */
    public List<ErrorLog> getLogsByTimeRange(LocalDateTime start, LocalDateTime end) {
        return errorLogRepository.findByTimestampBetween(start, end);
    }

    /**
     * Get logs by level and time range
     */
    public List<ErrorLog> getLogsByLevelAndTimeRange(String level, LocalDateTime start, LocalDateTime end) {
        return errorLogRepository.findByLevelAndTimestampBetween(level, start, end);
    }

    /**
     * Get recent logs (last N hours)
     */
    public List<ErrorLog> getRecentLogs(int hours) {
        LocalDateTime end = LocalDateTime.now();
        LocalDateTime start = end.minusHours(hours);
        return errorLogRepository.findByTimestampBetween(start, end);
    }

    /**
     * Convert exception stack trace to string
     */
    private String getStackTrace(Exception exception) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        return sw.toString();
    }

    /**
     * Delete old logs (older than specified days)
     */
    public void deleteOldLogs(int days) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(days);
        List<ErrorLog> oldLogs = errorLogRepository.findByTimestampBetween(
            LocalDateTime.of(2000, 1, 1, 0, 0), cutoff
        );
        if (!oldLogs.isEmpty()) {
            errorLogRepository.deleteAll(oldLogs);
            logger.info("Deleted {} old error logs", oldLogs.size());
        }
    }
}

