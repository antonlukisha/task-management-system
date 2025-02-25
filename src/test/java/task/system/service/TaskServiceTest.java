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
import task.system.dto.TaskDTO;
import task.system.entity.Task;
import task.system.exception.implementations.TaskException;
import task.system.mapper.TaskMapper;
import task.system.repository.TaskRepository;
import task.system.service.implementations.TaskDataService;
import task.system.service.implementations.TaskService;
import task.system.type.Priority;
import task.system.type.Status;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        when(taskMapper.toTaskEntity(any(TaskDTO.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        boolean result = taskService.createTask(taskDTO);
        assertTrue(result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void createTask_Failure_DB() {
        when(taskMapper.toTaskEntity(any(TaskDTO.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class)))
                .thenThrow(TaskException.of(HttpStatus.BAD_REQUEST, "DB Exception"));
        boolean result = taskService.createTask(taskDTO);
        assertFalse(result);
        verify(taskRepository, times(1)).save(any(Task.class));
    }
    @Test
    void createTask_Failure_Mapper() {
        when(taskMapper.toTaskEntity(any(TaskDTO.class)))
                .thenThrow(TaskException.of(HttpStatus.BAD_REQUEST, "Mapper Exception"));
        boolean result = taskService.createTask(taskDTO);
        assertFalse(result);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void getTaskById_Success() {
        when(taskDataService.getTaskFromCacheOrDatabase(anyLong())).thenReturn(task);
        when(taskMapper.toTaskDTO(any(Task.class))).thenReturn(taskDTO);
        TaskDTO result = taskService.getTaskById(1L);
        assertNotNull(result);
        assertEquals("Test Title", result.getTitle());
    }

    @Test
    void getTaskById_Failure() {
        when(taskDataService.getTaskFromCacheOrDatabase(anyLong())).thenReturn(null);
        TaskDTO result = taskService.getTaskById(1L);
        assertNull(result);
    }

    @Test
    void deleteTask_Success() {
        doNothing().when(taskDataService).deleteTaskFromCacheOrDatabase(anyLong());
        boolean result = taskService.deleteTask(1L);
        assertTrue(result);
        verify(taskDataService, times(1)).deleteTaskFromCacheOrDatabase(anyLong());
    }

    @Test
    void deleteTask_Failure() {
        doThrow(TaskException.of(HttpStatus.BAD_REQUEST, "Delete Exception"))
                .when(taskDataService).deleteTaskFromCacheOrDatabase(anyLong());
        boolean result = taskService.deleteTask(1L);
        assertFalse(result);
        verify(taskDataService, times(1)).deleteTaskFromCacheOrDatabase(anyLong());
    }

    @Test
    void updateTaskById_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        doNothing().when(taskDataService).updateTaskFromCacheOrDatabase(anyLong(), any(Task.class));
        boolean result = taskService.updateTaskById(1L, taskDTO);
        assertTrue(result);
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskDataService, times(1))
                .updateTaskFromCacheOrDatabase(anyLong(), any(Task.class));
    }

    @Test
    void updateTaskById_Failure_NoFound() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        boolean result = taskService.updateTaskById(1L, taskDTO);
        assertFalse(result);
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskDataService, never()).updateTaskFromCacheOrDatabase(anyLong(), any(Task.class));
    }

    @Test
    void updateTaskById_Failure_TaskDataService() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        doThrow(TaskException.of(HttpStatus.BAD_REQUEST, "Update Exception"))
                .when(taskDataService).updateTaskFromCacheOrDatabase(anyLong(), any(Task.class));
        boolean result = taskService.updateTaskById(1L, taskDTO);
        assertFalse(result);
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskDataService, times(1))
                .updateTaskFromCacheOrDatabase(anyLong(), any(Task.class));
    }

    @Test
    void assignTaskToUser_Success() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        doNothing().when(taskDataService).updateTaskFromCacheOrDatabase(anyLong(), any(Task.class));
        boolean result = taskService.assignTaskToUser(1L, "example@example.com");
        assertTrue(result);
        verify(taskRepository, times(1)).findById(anyLong());
        verify(taskDataService, times(1))
                .updateTaskFromCacheOrDatabase(anyLong(), any(Task.class));
    }

    @Test
    void assignTaskToUser_Failure() {
        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        boolean result = taskService.assignTaskToUser(1L, "example@example.com");
        assertFalse(result);
        verify(taskRepository, times(1)).findById(anyLong());
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
