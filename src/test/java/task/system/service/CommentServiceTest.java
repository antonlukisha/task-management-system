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
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import task.system.dto.CommentDTO;
import task.system.entity.Comment;
import task.system.entity.Task;
import task.system.exception.implementations.CommentException;
import task.system.mapper.CommentMapper;
import task.system.repository.CommentRepository;
import task.system.service.implementations.CommentService;
import task.system.service.implementations.TaskDataService;
import task.system.type.Priority;
import task.system.type.Status;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {
    @InjectMocks
    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private TaskDataService taskDataService;

    private CommentDTO commentDTO;
    private Comment comment;
    private Task task;

    @BeforeEach
    void setUp() {
        task = Task.builder()
                .title("Test Title")
                .description("Test Description")
                .status(Status.COMPLETED)
                .priority(Priority.HIGH)
                .author("author@example.com")
                .assignee("assignee@example.com")
                .build();

        commentDTO = CommentDTO.builder()
                .text("Test Text")
                .author("author@example.com")
                .build();

        comment = Comment.builder()
                .text("Test Text")
                .author("author@example.com")
                .task(task)
                .build();
    }

    @Test
    void getByTask_Success() {
        List<Comment> comments = Collections.singletonList(comment);
        when(commentRepository.findByTaskId(1L, PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(comments));
        when(commentMapper.toCommentDTO(any(Comment.class))).thenReturn(commentDTO);
        List<CommentDTO> result = commentService.getByTask(1L, 0, 10);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor()).isEqualTo("author@example.com");
    }

    @Test
    void add_Success() {
        when(authentication.getName()).thenReturn("author@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(taskDataService.getTaskFromCacheOrDatabase(1L)).thenReturn(task);
        when(commentMapper.toCommentEntity(any(CommentDTO.class), any(Task.class)))
                .thenReturn(comment);
        boolean result = commentService.add(1L, commentDTO);
        assertTrue(result);
        verify(commentRepository, times(1)).save(any(Comment.class));
        SecurityContextHolder.clearContext();
    }

    @Test
    void add_Failure_BadRequest() {
        when(authentication.getName()).thenReturn("another@example.com");
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        CommentException exception = assertThrows(CommentException.class, () -> {
            commentService.add(1L, commentDTO);
        });
        assertThat(exception.getCode()).isEqualTo(HttpStatus.FORBIDDEN);
        SecurityContextHolder.clearContext();
    }
}
