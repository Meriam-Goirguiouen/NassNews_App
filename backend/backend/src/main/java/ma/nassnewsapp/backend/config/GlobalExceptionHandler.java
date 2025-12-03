package ma.nassnewsapp.backend.config;

import jakarta.servlet.http.HttpServletRequest;
import ma.nassnewsapp.backend.services.ErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final ErrorLogService errorLogService;

    @Autowired
    public GlobalExceptionHandler(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    /**
     * Handle IllegalArgumentException (validation errors, business logic errors)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        
        logger.warn("IllegalArgumentException: {}", ex.getMessage());
        
        // Log to database
        errorLogService.logErrorWithContext(
            "Validation",
            ex.getMessage(),
            ex,
            null,
            request.getRequestURI(),
            request.getMethod()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", ex.getMessage());
        response.put("error", "Validation Error");
        
        return ResponseEntity.badRequest().body(response);
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        logger.error("Unhandled exception: ", ex);
        
        // Log to database
        errorLogService.logErrorWithContext(
            "System",
            ex.getMessage() != null ? ex.getMessage() : "Unknown error occurred",
            ex,
            null,
            request.getRequestURI(),
            request.getMethod()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "An unexpected error occurred");
        response.put("error", "Internal Server Error");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

