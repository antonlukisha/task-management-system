package task.system.exception.implementations;

import org.springframework.http.HttpStatus;
import task.system.exception.BaseTaskServiceException;

public class UserException extends BaseTaskServiceException {
  private UserException(HttpStatus code, String message) {
    super(code, message);
  }

  public static UserException of(HttpStatus code, String message) {
    return new UserException(code, message);
  }
}
