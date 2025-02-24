package task.system.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class BaseTaskServiceException extends RuntimeException {
    private final HttpStatus code;
    private final String message;

    protected BaseTaskServiceException(HttpStatus code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * Method return formatted message about exception.
     * @return string with exception details.
     */
    public String getFormattedMessage() {
        return String.format("%s: Code: %d Message: %s",
                this.getClass().getSimpleName(), code.value(), message);
    }
}
