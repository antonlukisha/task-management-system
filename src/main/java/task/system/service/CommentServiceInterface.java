package task.system.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import task.system.dto.CommentDTO;

import java.util.List;

@Tag(name = "CommentServiceInterface", description = "Interface of comments service")
public interface CommentServiceInterface {
    /**
     * METHOD: getByTask
     * Method to fetch comments for a given task.
     *
     * @param taskId Identity of task.
     * @param page The page number for pagination.
     * @param size The number of comments per page.
     * @return A list of {@link CommentDTO} for the specified task.
     */
    @Operation(
            summary = "Get comments for task",
            description = "Gets all comments for a given task with pagination support."
    )
    List<CommentDTO> getByTask(Long taskId, int page, int size);

    /**
     * METHOD: add
     * Method to add a new comment to a task.
     *
     * @param taskId Identity of task.
     * @param dto Data transfer object containing the comment details {@link CommentDTO}.
     * @return {@code true} if the comment was successfully added, {@code false} otherwise.
     */
    @Operation(
            summary = "Add comment to task",
            description = "Adds a comment to a task. The comment must be associated with the task."
    )
    boolean add(Long taskId, CommentDTO dto);
}
