package task.system.service.implementations;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.system.dto.CommentDTO;
import task.system.entity.Comment;
import task.system.entity.Task;
import task.system.exception.implementations.CommentException;
import task.system.mapper.CommentMapper;
import task.system.repository.CommentRepository;
import task.system.service.CommentServiceInterface;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Tag(name = "CommentService", description = "Service with business logic for working with comments")
@RequiredArgsConstructor
public class CommentService implements CommentServiceInterface {
    private final CommentRepository commentRepository;
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final TaskDataService taskDataService;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CommentDTO> getByTask(Long taskId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Attempting to get comments by task ID...");
        Page<Comment> comments = commentRepository.findByTaskId(taskId, pageable);
        if (comments.isEmpty()) {
            logger.info("Task does not have any comments");
            return Collections.emptyList();
        }
        logger.info("Task has {} comments", comments.getSize());
        return comments.getContent().stream()
                .map(commentMapper::toCommentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean add(Long taskId, CommentDTO commentDTO) {
        logger.info("Attempting to add a comment to task ID: {}...", taskId);
        logger.info("Getting username from Security Context...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        if (!currentUsername.equals(commentDTO.getAuthor())) {
            logger.error("Incorrect username in DTO: expected {}, but found {}", currentUsername, commentDTO.getAuthor());
            throw CommentException.of(HttpStatus.BAD_REQUEST, "Incorrect username in DTO");
        }

        Task task = taskDataService.getTaskFromCacheOrDatabase(taskId);

        if (!isUserAuthorizedForTask(currentUsername, task)) {
            logger.error("User {} is not authorized to add a comment to task ID {}", currentUsername, taskId);
            throw CommentException.of(HttpStatus.FORBIDDEN, "You are not authorized to add a comment to this task");
        }

        logger.info("Create and save new comment for task with ID {}", taskId);
        Comment comment = commentMapper.toCommentEntity(commentDTO, task);

        commentRepository.save(comment);
        logger.info("Successfully added comment to task ID: {}", taskId);

        return true;
    }

    private boolean isUserAuthorizedForTask(String username, Task task) {
        return task.getAuthor().equals(username)
                || task.getAssignee().equals(username);
    }


}
