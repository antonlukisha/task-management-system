package task.system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    private final TaskService taskService;
    /**
     * METHOD POST: createTask.
     * This method create a new task.
     *
     * @param taskDTO Data transfer object of task.
     * @return
     *  - If task was created response has status CREATED (201);
     *  - If task was not created response has status BAD_REQUEST (400);
     */
    @PostMapping
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Creating a new task",
            description = "Allow to create new task and add to db"
    )
    public ResponseEntity<HttpStatus> createTask(@Valid @RequestBody TaskDTO taskDTO) {
        boolean result = taskService.createTask(taskDTO);
        return new ResponseEntity<>((result ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST));
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
    @PutMapping("/id")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Editing an existent task",
            description = "Allow to update existent task and add changes to db"
    )
    public ResponseEntity<HttpStatus> updateTask(@Valid @RequestParam("id") Long id,
                                                 @Valid @RequestBody TaskDTO taskDTO) {
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
    @DeleteMapping("/id")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Deleting a task",
            description = "Allow to delete existent task and update changes in db"
    )
    public ResponseEntity<HttpStatus> deleteTask(@Valid @RequestParam("id") Long id) {
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
    @GetMapping("/id")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Getting a task",
            description = "Allow to get task from db"
    )
    public ResponseEntity<TaskDTO> getTaskById(@Valid @RequestParam("id") Long id) {
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
    @PutMapping("/assign/id")
    @SecurityRequirement(name = "JWT")
    @Operation(
            summary = "Assign a user as performer of task",
            description = "Allow to assign user as performer of task and save changes to db"
    )
    public ResponseEntity<Boolean> assignTaskToUser(@Valid @RequestParam("task_id") Long taskId,
                                                    @Valid @RequestParam("assignee") String assignee) {
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
    @GetMapping("/all")
    @Operation(
            summary = "Getting all tasks",
            description = "Allow to get all tasks with pagination from db"
    )
    public ResponseEntity<List<TaskDTO>> getAllTasks(@Valid @RequestParam(value = "page", defaultValue = "0") int page,
                                                  @Valid @RequestParam(value = "size", defaultValue = "10") int size) {
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
    @GetMapping("/author")
    @Operation(
            summary = "Getting tasks by author",
            description = "Allow to get tasks by author with pagination from db"
    )
    public ResponseEntity<List<TaskDTO>> getTasksByAuthor(@Valid @RequestParam("author") String author,
                                                       @Valid @RequestParam(value = "page", defaultValue = "0") int page,
                                                       @Valid @RequestParam(value = "size", defaultValue = "10") int size) {
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
    @GetMapping("/assignee")
    @Operation(
            summary = "Getting all tasks by assignee",
            description = "Allow to get all task by assignee with pagination from db"
    )
    public ResponseEntity<List<TaskDTO>> getTasksByAssignee(@Valid @RequestParam("assignee") String assignee,
                                                         @Valid @RequestParam(value = "page", defaultValue = "0") int page,
                                                         @Valid @RequestParam(value = "size", defaultValue = "10") int size) {
        List<TaskDTO> tasks = taskService.getTasksByAssignee(assignee, page, size);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }
}
