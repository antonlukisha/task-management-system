package task.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.system.dto.CommentDTO;
import task.system.service.implementations.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "CommentController", description = "Controller as the endpoint of the REST api for working with comments")
@RequestMapping("/api/tasks/comments")
public class CommentController {

    private final CommentService commentService;

    /**
     * METHOD POST: addComment.
     * This method add comment at task by id.
     *
     * @param taskId Identity of task.
     * @param commentDTO Data transfer object of comment.
     * @return
     *  - If comment was added response has status CREATED (201);
     *  - If comment was not added response has status BAD_REQUEST (400);
     */
    @PostMapping("/protect/add")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Adding a comment",
            description = "Allow to add comment to task and save to db",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully added comment"),
                    @ApiResponse(responseCode = "400", description = "Comment not added")
            }
    )
    public ResponseEntity<HttpStatus> addComment(@Valid @RequestParam("task_id") Long taskId,
                                              @Valid @RequestBody CommentDTO commentDTO) {
        boolean result = commentService.add(taskId, commentDTO);
        return new ResponseEntity<>((result ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST));
    }

    /**
     * METHOD GET: getCommentsByTask.
     * This method get all comments of task.
     *
     * @param taskId Identity of task.
     * @param page Page number.
     * @param size Size number.
     * @return response with status OK (200) and list of comments.
     */
    @GetMapping("/public/task/id")
    @Operation(
            summary = "Getting comments of task",
            description = "Allow to get comments of task by ID with pagination",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of comments")
            }
    )
    public ResponseEntity<List<CommentDTO>> getCommentsByTask(@Valid @RequestParam("task_id") Long taskId,
                                                           @Valid @RequestParam(value = "page", defaultValue = "0") int page,
                                                           @Valid @RequestParam(value = "size", defaultValue = "10") int size) {
        List<CommentDTO> comments = commentService.getByTask(taskId, page, size);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
}

