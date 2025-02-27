package task.system.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import task.system.dto.TaskDTO;
import task.system.entity.Task;
import task.system.exception.implementations.TaskException;
import task.system.mapper.TaskMapper;
import task.system.repository.TaskRepository;
import task.system.service.implementations.TaskDataService;
import task.system.service.implementations.TaskService;
import task.system.type.Priority;
import task.system.type.Status;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskService taskService;
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskDataService taskDataService;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private TaskMapper taskMapper;

    private TaskDTO taskDTO;
    private Task task;

    @BeforeEach
    void setUp() {
        taskDTO = TaskDTO.builder()
                .title("Test Title")
                .description("Test Description")
                .status("COMPLETED")
                .priority("HIGH")
                .author("author@example.com")
                .assignee("assignee@example.com")
                .build();

        task = Task.builder()
                .id(1L)
                .title("Test Title")
                .description("Test Description")
                .status(Status.COMPLETED)
                .priority(Priority.HIGH)
                .author("author@example.com")
                .assignee("assignee@example.com")
                .build();
    }

    @Test
    void createTask_Success() {
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(taskMapper.toTaskEntity(taskDTO)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(task);
        Long taskId = taskService.createTask(taskDTO);
        assertNotNull(taskId);
        assertEquals(1L, taskId);
        verify(taskRepository, times(1)).save(task);
        SecurityContextHolder.clearContext();
    }

    @Test
    void createTask_Failure() {
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        TaskException exception = assertThrows(TaskException.class, () -> taskService.createTask(taskDTO));
        assertEquals(HttpStatus.FORBIDDEN, exception.getCode());
        SecurityContextHolder.clearContext();
    }

    @Test
    void getTaskById_Success() throws TaskException {
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(taskDataService.getTaskFromCacheOrDatabase(1L)).thenReturn(task);
        when(taskMapper.toTaskDTO(task)).thenReturn(taskDTO);
        TaskDTO result = taskService.getTaskById(1L);
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
        SecurityContextHolder.clearContext();
    }

    @Test
    void getTaskById_Failure() {
        TaskException exception = assertThrows(TaskException.class, () -> taskService.getTaskById(1L));
        assertEquals(HttpStatus.FORBIDDEN, exception.getCode());
    }

    @Test
    void deleteTask_Success() throws TaskException {
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        boolean result = taskService.deleteTask(1L);
        assertTrue(result);
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDeleteTask_Failure() {
        when(authentication.getAuthorities()).thenReturn((Collection) Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        TaskException exception = assertThrows(TaskException.class, () -> taskService.deleteTask(1L));
        assertEquals(HttpStatus.FORBIDDEN, exception.getCode());
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllTasks_Success() {
        List<Task> tasks = Collections.singletonList(task);
        when(taskRepository.findAll(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(tasks));
        when(taskMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);
        List<TaskDTO> result = taskService.getAllTasks(0, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor()).isEqualTo("author@example.com");
    }

    @Test
    void getTasksByAuthor_Success() {
        List<Task> tasks = Collections.singletonList(task);
        when(taskRepository.findAllByAuthor("author@example.com", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(tasks));
        when(taskMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);
        List<TaskDTO> result = taskService.getTasksByAuthor("author@example.com", 0, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAssignee()).isEqualTo("assignee@example.com");
    }

    @Test
    void getTasksByAssignee_Success() {
        List<Task> tasks = Collections.singletonList(task);
        when(taskRepository.findAllByAssignee("assignee@example.com", PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(tasks));
        when(taskMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);
        List<TaskDTO> result = taskService.getTasksByAssignee("assignee@example.com", 0, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor()).isEqualTo("author@example.com");
    }
}
