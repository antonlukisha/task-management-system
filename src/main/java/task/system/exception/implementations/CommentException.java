package task.system.exception.implementations;

import org.springframework.http.HttpStatus;
import task.system.exception.BaseTaskServiceException;


public class CommentException extends BaseTaskServiceException {
    private CommentException(HttpStatus code, String message) {
        super(code, message);
    }

    public static CommentException of(HttpStatus code, String message) {
        return new CommentException(code, message);
    }
}
