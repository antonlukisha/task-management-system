package task.system.exception.implementations;

import org.springframework.http.HttpStatus;
import task.system.exception.BaseTaskServiceException;

public class JwtTokenException extends BaseTaskServiceException {
    private JwtTokenException(HttpStatus code, String message) {
        super(code, message);
    }

    public static JwtTokenException of(HttpStatus code, String message) {
        return new JwtTokenException(code, message);
    }
}
