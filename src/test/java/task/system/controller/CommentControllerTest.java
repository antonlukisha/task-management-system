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
import task.system.dto.CommentDTO;
import task.system.service.implementations.CommentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class CommentControllerTest {
    private MockMvc mock;
    @InjectMocks
    private CommentController commentController;
    @Mock
    private CommentService commentService;

    private CommentDTO commentDTO;

    @BeforeEach
    void setUp() {
        commentDTO = CommentDTO.builder()
                .text("Test Text")
                .author("example@mail.com")
                .build();
        mock = MockMvcBuilders.standaloneSetup(commentController).build();
    }

    @Test
    void addComment_Created() throws Exception {
        when(commentService.add(any(Long.class), any(CommentDTO.class))).thenReturn(true);
        mock.perform(post("/api/tasks/comments/protect/add?task_id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void addComment_BadRequest() throws Exception {
        when(commentService.add(any(Long.class), any(CommentDTO.class))).thenReturn(false);
        mock.perform(post("/api/tasks/comments/protect/add?task_id=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(commentDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommentsByTask_Ok() throws Exception {
        mock.perform(get("/api/tasks/comments/public/task/id?task_id=1&page=0&size=10"))
                .andExpect(status().isOk());
    }
}
