package task.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.system.dto.TaskDTO;
import task.system.service.implementations.TaskService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "TaskController", description = "Controller as the endpoint of the REST api for working with tasks")
@RequestMapping("/api/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskService taskService;
    /**
     * METHOD POST: createTask.
     * This method create a new task.
     *
     * @param taskDTO Data transfer object of task.
     * @return
     *  - If task was created response has status CREATED (201) and body with ID in string format;
     */
    @PostMapping("/protect")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Creating a new task",
            description = "Allow to create new task and add to db",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Task successfully created"),
            }
    )
    public ResponseEntity<String> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        logger.info("Received create task request: {}", taskDTO.toString());
        Long id = taskService.createTask(taskDTO);
        return new ResponseEntity<>(String.format(id.toString()), HttpStatus.CREATED);
    }

    /**
     * METHOD PUT: updateTask.
     * This method update existent task by id.
     *
     * @param id Identity of task.
     * @return
     *  - If task was updated response has status OK (200);
     *  - If task was not updated response has status BAD_REQUEST (400);
     */
    @PutMapping("/protect/id")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Editing an existent task",
            description = "Allow to update existent task and add changes to db",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully updated task"),
                    @ApiResponse(responseCode = "400", description = "Task not updated")
            }
    )
    public ResponseEntity<HttpStatus> updateTask(@Valid @RequestParam("id") Long id,
                                                 @Valid @RequestBody TaskDTO taskDTO) {
        logger.info("Received update task request: {}, {}", id, taskDTO.toString());
        boolean result = taskService.updateTaskById(id, taskDTO);
        return new ResponseEntity<>((result ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
    }

    /**
     * METHOD DELETE: deleteTask.
     * This method delete task by id.
     *
     * @param id Identity of task.
     * @return
     *  - If task was deleted response has status NO_CONTENT (204);
     *  - If task was not deleted response has status BAD_REQUEST (400);
     */
    @DeleteMapping("/protect/id")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Deleting a task",
            description = "Allow to delete existent task and update changes in db",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successfully deleted task"),
                    @ApiResponse(responseCode = "400", description = "Task not deleted")
            }
    )
    public ResponseEntity<HttpStatus> deleteTask(@Valid @RequestParam("id") Long id) {
        logger.info("Received delete task request: {}", id);
        boolean result = taskService.deleteTask(id);
        return new ResponseEntity<>((result ? HttpStatus.NO_CONTENT : HttpStatus.BAD_REQUEST));
    }

    /**
     * METHOD GET: getTaskById.
     * This method get task by id.
     *
     * @param id Identity of task.
     * @return response with status OK (200) and body with dto.
     */
    @GetMapping("/protect/id")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Getting a task",
            description = "Allow to get task from db",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetch task"),
            }
    )
    public ResponseEntity<TaskDTO> getTaskById(@Valid @RequestParam("id") Long id) {
        logger.info("Received get task by id request: {}", id);
        TaskDTO foundTask = taskService.getTaskById(id);
        return new ResponseEntity<>(foundTask, HttpStatus.OK);
    }

    /**
     * METHOD PUT: assignTaskToUser.
     * This method assign task to user.
     *
     * @param taskId Identity of task.
     * @param assignee User whom the task will be assigned.
     * @return
     *  - If performer was assigned  to task response has status OK (200);
     *  - If performer was not assigned to task response has status BAD_REQUEST (404);
     */
    @PutMapping("/protect/assign/id")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Assign a user as performer of task",
            description = "Allow to assign user as performer of task and save changes to db",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully assigned performer for task"),
                    @ApiResponse(responseCode = "404", description = "Task not assigned performer")
            }
    )
    public ResponseEntity<Boolean> assignTaskToUser(@Valid @RequestParam("task_id") Long taskId,
                                                    @Valid @RequestParam("assignee") String assignee) {
        logger.info("Received assign task to user request: {}, {}", taskId, assignee);
        boolean result = taskService.assignTaskToUser(taskId, assignee);
        return new ResponseEntity<>((result ? HttpStatus.OK : HttpStatus.BAD_REQUEST));
    }

    /**
     * METHOD GET: getAllTasks.
     * This method get all tasks.
     *
     * @param page Page number.
     * @param size Size number.
     * @return response with status OK (200) and list of tasks.
     */
    @GetMapping("/public/all")
    @Operation(
            summary = "Getting all tasks",
            description = "Allow to get all tasks with pagination from db",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetch tasks"),
            }
    )
    public ResponseEntity<List<TaskDTO>> getAllTasks(@Valid @RequestParam(value = "page", defaultValue = "0") int page,
                                                  @Valid @RequestParam(value = "size", defaultValue = "10") int size) {
        logger.info("Received get all tasks request: {}, {}", page, size);
        List<TaskDTO> tasks = taskService.getAllTasks(page, size);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * METHOD GET: getTasksByAuthor.
     * This method get all tasks by author.
     *
     * @param author Username of author.
     * @param page Page number.
     * @param size Size number.
     * @return response with status OK (200) and list of tasks.
     */
    @GetMapping("/public/author")
    @Operation(
            summary = "Getting tasks by author",
            description = "Allow to get tasks by author with pagination from db",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetch tasks"),
            }
    )
    public ResponseEntity<List<TaskDTO>> getTasksByAuthor(@Valid @RequestParam("author") String author,
                                                       @Valid @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @Valid @RequestParam(value = "size", defaultValue = "10") int size) {
        logger.info("Received get all tasks by author request: {}, {}, {}", author, page, size);
        List<TaskDTO> tasks = taskService.getTasksByAuthor(author, page, size);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * METHOD GET: getTasksByAssignee.
     * This method get all tasks by assignee.
     *
     * @param assignee Username of assignee.
     * @param page Page number.
     * @param size Size number.
     * @return response with status OK (200) and list of tasks.
     */
    @GetMapping("/public/assignee")
    @Operation(
            summary = "Getting all tasks by assignee",
            description = "Allow to get all task by assignee with pagination from db",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully fetch tasks"),
            }
    )
    public ResponseEntity<List<TaskDTO>> getTasksByAssignee(@Valid @RequestParam("assignee") String assignee,
                                                         @Valid @RequestParam(value = "page", defaultValue = "0") int page,
                                                         @Valid @RequestParam(value = "size", defaultValue = "10") int size) {
        logger.info("Received get all tasks by assignee request: {}, {}, {}", assignee, page, size);
        List<TaskDTO> tasks = taskService.getTasksByAssignee(assignee, page, size);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
}
