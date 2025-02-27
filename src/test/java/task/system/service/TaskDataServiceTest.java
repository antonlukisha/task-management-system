package task.system.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.RedisConnectionFailureException;
import task.system.entity.Task;
import task.system.exception.implementations.TaskException;
import task.system.repository.TaskRepository;
import task.system.service.implementations.TaskDataService;
import task.system.type.Priority;
import task.system.type.Status;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@ExtendWith(MockitoExtension.class)
public class TaskDataServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;
    @Mock
    private TaskRepository taskRepository;
    @InjectMocks
    private TaskDataService taskDataService;

    private Task task;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .status(Status.IN_PROGRESS)
                .priority(Priority.MEDIUM)
                .author("author@mail.com")
                .assignee("assignee@mail.com")
                .build();
    }
    
}
