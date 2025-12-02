package ma.nassnewsapp.backend.controllers;

import ma.nassnewsapp.backend.entities.ErrorLog;
import ma.nassnewsapp.backend.services.ErrorLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/logs")
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    @Autowired
    public ErrorLogController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    /**
     * Get all error logs
     */
    @GetMapping
    public ResponseEntity<List<ErrorLog>> getAllLogs(
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) Integer hours) {
        
        List<ErrorLog> logs;
        
        if (hours != null && hours > 0) {
            // Get recent logs
            logs = errorLogService.getRecentLogs(hours);
        } else if (level != null && !level.isEmpty()) {
            // Filter by level
            logs = errorLogService.getLogsByLevel(level);
        } else if (service != null && !service.isEmpty()) {
            // Filter by service
            logs = errorLogService.getLogsByService(service);
        } else {
            // Get all logs
            logs = errorLogService.getAllLogs();
        }
        
        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs by level
     */
    @GetMapping("/level/{level}")
    public ResponseEntity<List<ErrorLog>> getLogsByLevel(@PathVariable String level) {
        List<ErrorLog> logs = errorLogService.getLogsByLevel(level);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs by service
     */
    @GetMapping("/service/{service}")
    public ResponseEntity<List<ErrorLog>> getLogsByService(@PathVariable String service) {
        List<ErrorLog> logs = errorLogService.getLogsByService(service);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get logs within time range
     */
    @GetMapping("/range")
    public ResponseEntity<List<ErrorLog>> getLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        List<ErrorLog> logs = errorLogService.getLogsByTimeRange(start, end);
        return ResponseEntity.ok(logs);
    }

    /**
     * Get error statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getLogStatistics() {
        List<ErrorLog> allLogs = errorLogService.getAllLogs();
        
        long errorCount = allLogs.stream().filter(log -> "ERROR".equals(log.getLevel())).count();
        long warnCount = allLogs.stream().filter(log -> "WARN".equals(log.getLevel())).count();
        long infoCount = allLogs.stream().filter(log -> "INFO".equals(log.getLevel())).count();
        
        List<ErrorLog> recentErrors = errorLogService.getRecentLogs(24);
        long recentErrorCount = recentErrors.stream().filter(log -> "ERROR".equals(log.getLevel())).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalLogs", allLogs.size());
        stats.put("errorCount", errorCount);
        stats.put("warnCount", warnCount);
        stats.put("infoCount", infoCount);
        stats.put("recentErrors24h", recentErrorCount);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Delete old logs
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, String>> deleteOldLogs(@RequestParam(defaultValue = "30") int days) {
        errorLogService.deleteOldLogs(days);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Deleted logs older than " + days + " days");
        return ResponseEntity.ok(response);
    }
}

