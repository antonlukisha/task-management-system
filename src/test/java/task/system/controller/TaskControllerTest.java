package task.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task.system.dto.TaskDTO;
import task.system.service.implementations.TaskService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TaskControllerTest {
    private MockMvc mock;
    @InjectMocks
    private TaskController taskController;
    @Mock
    private TaskService taskService;

    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        taskDTO = TaskDTO.builder()
                .title("Test Title")
                .description("Test Description")
                .author("author@mail.com")
                .assignee("assignee@mail.com")
                .build();
        mock = MockMvcBuilders.standaloneSetup(taskController).build();
    }

    @Test
    void createTask_Created() throws Exception {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(true);
        mock.perform(post("/api/tasks/protect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void createTask_BadRequest() throws Exception {
        when(taskService.createTask(any(TaskDTO.class))).thenReturn(false);
        mock.perform(post("/api/tasks/protect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateTask_Ok() throws Exception {
        when(taskService.updateTaskById(any(Long.class), any(TaskDTO.class))).thenReturn(true);
        mock.perform(put("/api/tasks/protect/id?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void updateTask_BadRequest() throws Exception {
        when(taskService.updateTaskById(any(Long.class), any(TaskDTO.class))).thenReturn(false);
        mock.perform(put("/api/tasks/protect/id?id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(taskDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTask_NoContent() throws Exception {
        when(taskService.deleteTask(any(Long.class))).thenReturn(true);
        mock.perform(delete("/api/tasks/protect/id?id=1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteTask_BadRequest() throws Exception {
        when(taskService.deleteTask(any(Long.class))).thenReturn(false);
        mock.perform(delete("/api/tasks/protect/id?id=1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getTaskById_Ok() throws Exception {
        when(taskService.getTaskById(any(Long.class))).thenReturn(taskDTO);

        mock.perform(get("/api/tasks/protect/id?id=1"))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.author").value("author@mail.com"))
                .andExpect(jsonPath("$.assignee").value("assignee@mail.com"))
                .andExpect(status().isOk());


    }

    @Test
    void assignTaskToUser_Ok() throws Exception {
        when(taskService.assignTaskToUser(any(Long.class), any(String.class))).thenReturn(true);
        mock.perform(put("/api/tasks/protect/assign/id?task_id=1&assignee=example@mail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void assignTaskToUser_BadRequest() throws Exception {
        when(taskService.assignTaskToUser(any(Long.class), any(String.class))).thenReturn(false);
        mock.perform(put("/api/tasks/protect/assign/id?task_id=1&assignee=example@mail.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTasks_Ok() throws Exception {
        mock.perform(get("/api/tasks/public/all?page=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    void getTasksByAuthor_Ok() throws Exception {
        mock.perform(get("/api/tasks/public/author?author=example@mail.com&page=0&size=10"))
                .andExpect(status().isOk());
    }

    @Test
    void getTasksByAssignee_Ok() throws Exception {
        mock.perform(get("/api/tasks/public/assignee?assignee=example@mail.com&page=0&size=10"))
                .andExpect(status().isOk());
    }
}
