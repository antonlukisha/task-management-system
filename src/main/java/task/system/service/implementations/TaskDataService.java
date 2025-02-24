package task.system.service.implementations;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.system.entity.Task;
import task.system.exception.implementations.TaskException;
import task.system.repository.TaskRepository;
import task.system.service.TaskDataServiceInterface;

import java.util.concurrent.TimeUnit;

@Service
@Tag(name = "TaskDataService", description = "Task service containing methods for interacting with the cache and database")
@RequiredArgsConstructor
public class TaskDataService implements TaskDataServiceInterface {
    private static final Logger logger = LoggerFactory.getLogger(TaskDataService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final TaskRepository taskRepository;

    @Override
    @Transactional(readOnly = true)
    public Task getTaskFromCacheOrDatabase(Long taskId) {
        logger.info("Fetching task by ID {} from db or cache...", taskId);
        Task task = null;
        Object cachedTaskObj = redisTemplate.opsForValue().get("task:" + taskId);
        if (cachedTaskObj instanceof Task) {
            logger.info("Task found in cache: {}", taskId);
            task = (Task) cachedTaskObj;
        } else {
            logger.info("Task not found in cache, fetching from database: {}", taskId);
            task = taskRepository.findById(taskId).orElseThrow(() -> {
                logger.error("Task by ID {} not found", taskId);
                return TaskException.of(HttpStatus.NOT_FOUND, "Task not found");
            });
            redisTemplate.opsForValue().set("task:" + taskId, task, 24, TimeUnit.HOURS);
            logger.info("Task with ID {} fetched from database and cached", taskId);
        }
        return task;
    }

    @Override
    @Transactional
    public void deleteTaskFromCacheOrDatabase(Long taskId) {
        logger.info("Removing task by ID {} from db or cache...", taskId);
        redisTemplate.delete("task:" + taskId);
        taskRepository.deleteById(taskId);
        logger.info("Task successfully deleted from db or cache");
    }

    @Override
    @Transactional
    public void updateTaskFromCacheOrDatabase(Long taskId, Task task) {
        logger.info("Updating task with ID {} from db or cache...", taskId);
        redisTemplate.opsForValue().set("task:" + taskId, task, 24, TimeUnit.HOURS);
        taskRepository.save(task);
        logger.info("Task successfully updated from db or cache");
    }
}
