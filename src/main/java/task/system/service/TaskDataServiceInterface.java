package task.system.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import task.system.entity.Task;

@Tag(name = "TaskDataServiceInterface", description = "Interface of task data service")
public interface TaskDataServiceInterface {
    /**
     * METHOD: getTaskFromCacheOrDatabase
     * Method to fetch a task either from cache or from the database.
     *
     * @param id Identity of task.
     * @return {@link Task} - The task with the given ID.
     */
    @Operation(
            summary = "Get task from cache or db",
            description = "Retrieves a task either from the cache or the database."
    )
    Task getTaskFromCacheOrDatabase(Long id);

    /**
     * METHOD: deleteTaskFromCacheOrDatabase
     * Method to delete a task from cache or from the database.
     *
     * @param id Identity of task.
     */
    @Operation(
            summary = "Delete task from cache or db",
            description = "Delete a task either from the cache and the database."
    )
    void deleteTaskFromCacheOrDatabase(Long id);

    /**
     * METHOD: updateTaskFromCacheOrDatabase
     * Method to update a task from cache or from the database.
     *
     * @param id Identity of task.
     */
    @Operation(
            summary = "Update task from cache or db",
            description = "Update a task either from the cache and the database."
    )
    void updateTaskFromCacheOrDatabase(Long id, Task task);
}
