package task.system.exception.implementations;

import org.springframework.http.HttpStatus;
import task.system.exception.BaseTaskServiceException;

public class TaskException extends BaseTaskServiceException  {
    private TaskException(HttpStatus code, String message) {
        super(code, message);
    }

    public static TaskException of(HttpStatus code, String message) {
        return new TaskException(code, message);
    }
}
