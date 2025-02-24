package task.system.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import task.system.dto.TaskDTO;
import task.system.entity.Task;

import java.util.List;

@Tag(name = "TaskServiceInterface", description = "Interface of tasks service")
public interface TaskServiceInterface {
    /**
     * METHOD: createTask.
     * This method creates a new and saves it in system.
     *
     * @param dto Task data transfer object of task.
     * @return boolean - {@code true} if the task was successfully created, {@code false} otherwise.
     */
    @Operation(
            summary = "Create a new task",
            description = "Creates a new task with the provided data."
    )
    boolean createTask(TaskDTO dto);

    /**
     * METHOD: getTaskById.
     * This method retrieves a task by ID.
     *
     * @param id Identity of the task.
     * @return TaskDTO - Task data transfer object.
     */
    @Operation(
            summary = "Get task by ID",
            description = "Retrieve a task by its unique ID."
    )
    TaskDTO getTaskById(Long id);

    /**
     * METHOD: deleteTask.
     * This method deletes a task from the system by its ID.
     *
     * @param taskId The unique identity of the task.
     * @return boolean - {@code true} if the task was successfully deleted,
     *         {@code false} if the task could not be found or deleted.
     */
    @Operation(
            summary = "Delete task",
            description = "Deletes the task by its unique ID."
    )
    boolean deleteTask(Long taskId);

    /**
     * METHOD: updateTaskById.
     * This method updates an existing task with the new data provided in the dto.
     *
     * @param id Unique identity of the task.
     * @param dto The new task data to update the existing task.
     * @return boolean - {@code true} if the task was successfully updated,
     *         {@code false} otherwise.
     */
    @Operation(
            summary = "Update task",
            description = "Updates the task by its ID with the new data."
    )
    boolean updateTaskById(Long id, TaskDTO dto);

    /**
     * METHOD: assignTaskToUser.
     * This method assigns a task to a user by their ID.
     *
     * @param taskId The unique identity of the task.
     * @param assignee User whom the task will be assigned.
     * @return boolean - {@code true} if the task was successfully assigned to the user,
     *         {@code false} otherwise.
     */
    @Operation(
            summary = "Assign task to user",
            description = "Assigns the task to a specific user by their ID."
    )
    boolean assignTaskToUser(Long taskId, String assignee);

    /**
     * METHOD: getAllTasks.
     * This method retrieves a list of all tasks with pagination.
     *
     * @param page The page number to retrieve (starting from 0).
     * @param size The number of tasks per page.
     * @return List<TaskDTO> - A list of tasks in the specified page and size.
     */
    @Operation(
            summary = "Get all tasks",
            description = "Retrieves a paginated list of all tasks."
    )
    List<TaskDTO> getAllTasks(int page, int size);

    /**
     * METHOD: getTasksByAuthor.
     * This method retrieves a list of tasks created by a specific author with pagination.
     *
     * @param author Author username.
     * @param page The page number to retrieve (starting from 0).
     * @param size The number of tasks per page.
     * @return List<TaskDTO> - A list of tasks created by the specified author.
     */
    @Operation(
            summary = "Get tasks by author",
            description = "Retrieves tasks created by a specific author with pagination."
    )
    List<TaskDTO> getTasksByAuthor(String author, int page, int size);

    /**
     * METHOD: getTasksByAssignee.
     * This method retrieves a list of tasks assigned to a specific user with pagination.
     *
     * @param assignee Assignee username.
     * @param page The page number to retrieve (starting from 0).
     * @param size The number of tasks per page.
     * @return List<TaskDTO> - A list of tasks assigned to the specified user.
     */
    @Operation(
            summary = "Get tasks by assignee",
            description = "Retrieves tasks assigned to a specific user with pagination."
    )
    List<TaskDTO> getTasksByAssignee(String assignee, int page, int size);
}
