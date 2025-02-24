package task.system.exception;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import task.system.exception.implementations.CommentException;
import task.system.exception.implementations.JwtTokenException;
import task.system.exception.implementations.TaskException;
import task.system.exception.implementations.UserException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Tag(name = "GlobalExceptionHandler", description = "Controller for catching REST api exceptions")
public class GlobalExceptionHandler {
    /**
     * METHOD ExceptionHandler: handleProductValidationException.
     * Handles valid-related exceptions.
     *
     * @param exception MethodArgumentNotValidException.
     * @return ResponseEntity with error details and BAD_REQUEST (400).
     */
    @Operation(
            summary = "Handles valid-related exceptions",
            description = "Handles validation exceptions for method arguments and returns detailed error messages."
    )
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleProductValidationException(MethodArgumentNotValidException exception){
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String name = ((FieldError)error).getField();
            String message = error.getDefaultMessage();
            errors.put(name, message);
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * METHOD ExceptionHandler: handleUserException.
     * Handles user-related exceptions.
     *
     * @param exception UserException.
     * @return ResponseEntity with exception message and status code.
     */
    @Operation(
            summary = "Handles user-related exceptions",
            description = "Handles user exceptions for method arguments and returns detailed error messages."
    )
    @ExceptionHandler(UserException.class)
    public ResponseEntity<String> handleUserException(UserException exception){
        return buildErrorResponse(exception);
    }

    /**
     * METHOD ExceptionHandler: handleJwtTokenException.
     * Handles JWT-token-related exceptions.
     *
     * @param exception JwtTokenException.
     * @return ResponseEntity with exception message and status code.
     */
    @Operation(
            summary = "Handles JWT-token-related exceptions",
            description = "Handles JWT-token exceptions for method arguments and returns detailed error messages."
    )
    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<String> handleJwtTokenException(JwtTokenException exception){
        return buildErrorResponse(exception);
    }

    /**
     * METHOD ExceptionHandler: handleTaskException.
     * Handles task-related exceptions.
     *
     * @param exception TaskException.
     * @return ResponseEntity with exception message and status code.
     */
    @Operation(
            summary = "Handles task-related exceptions",
            description = "Handles task exceptions for method arguments and returns detailed error messages."
    )
    @ExceptionHandler(TaskException.class)
    public ResponseEntity<String> handleTaskException(TaskException exception){
        return buildErrorResponse(exception);
    }

    /**
     * METHOD ExceptionHandler: handleCommentException.
     * Handles comment-related exceptions.
     *
     * @param exception CommentException.
     * @return ResponseEntity with exception message and status code.
     */
    @Operation(
            summary = "Handles comment-related exceptions",
            description = "Handles comment exceptions for method arguments and returns detailed error messages."
    )
    @ExceptionHandler(CommentException.class)
    public ResponseEntity<String> handleCommentException(CommentException exception){
        return buildErrorResponse(exception);
    }

    /**
     * Helper method to build exception response
     * @param exception Exception.
     * @return ResponseEntity with exception message and status code.
     */
    private ResponseEntity<String> buildErrorResponse(Exception exception) {
        if (exception instanceof BaseTaskServiceException baseException) {
            return new ResponseEntity<>(baseException.getFormattedMessage(), baseException.getCode());
        }
        return new ResponseEntity<>("An unexpected error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

