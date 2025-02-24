package task.system.service.implementations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.system.dto.TaskDTO;
import task.system.entity.Task;
import task.system.mapper.TaskMapper;
import task.system.repository.TaskRepository;
import task.system.service.TaskServiceInterface;
import task.system.type.Priority;
import task.system.type.Status;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService implements TaskServiceInterface {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskDataService taskDataService;
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    @Transactional
    public boolean createTask(TaskDTO taskDTO) {
        try {
            logger.info("Creating new task...");
            Task task = taskMapper.toTaskEntity(taskDTO);
            taskRepository.save(task);
            logger.info("Task successfully created with ID: {}", task.getId());
            return true;
        } catch (Exception exception) {
            logger.error("Failed creating task: ", exception);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        logger.info("Fetching task with ID: {}...", id);
        Task task = taskDataService.getTaskFromCacheOrDatabase(id);
        return taskMapper.toTaskDTO(task);
    }

    @Override
    @Transactional
    public boolean deleteTask(Long taskId)  {
        try {
            logger.info("Deleting task by ID: {}...", taskId);
            taskDataService.deleteTaskFromCacheOrDatabase(taskId);
            logger.info("Task successfully deleted by ID: {}", taskId);
            return true;
        } catch (Exception exception) {
            logger.error("Failed deleting task by ID: {}", taskId, exception);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean updateTaskById(Long taskId, TaskDTO taskDTO) {
        try {
            logger.info("Updating task with ID: {}...", taskId);
            Optional<Task> existingTaskOptional = taskRepository.findById(taskId);
            if (existingTaskOptional.isPresent()) {
                Task task = existingTaskOptional.get();
                task.setTitle(taskDTO.getTitle());
                task.setDescription(taskDTO.getDescription());
                task.setStatus(Status.valueOf(taskDTO.getStatus()));
                task.setPriority(Priority.valueOf(taskDTO.getPriority()));
                task.setAssignee(taskDTO.getAssignee());
                taskDataService.updateTaskFromCacheOrDatabase(taskId, task);
                logger.info("Task with ID: {} successfully updated", taskId);
                return true;
            } else {
                logger.error("Task with ID: {} not found for update", taskId);
                return false;
            }
        } catch (Exception exception) {
            logger.error("Error updating task with ID: {}", taskId, exception);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean assignTaskToUser(Long taskId, String assignee) {
        try {
            logger.info("Assigning task by ID: {} to user: {}", taskId, assignee);
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isPresent()) {
                Task task = taskOptional.get();
                task.setAssignee(assignee);
                taskDataService.updateTaskFromCacheOrDatabase(taskId, task);
                logger.info("Task by ID: {} successfully assigned to user: {}", taskId, assignee);
                return true;
            } else {
                logger.error("Task with ID: {} not found", taskId);
                return false;
            }
        } catch (Exception exception) {
            logger.error("Error assigning task with ID: {} to user: {}", taskId, assignee, exception);
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Attempting to get all tasks with pagination...");
        Page<Task> tasks = taskRepository.findAll(pageable);
        if (tasks.isEmpty()) {
            logger.info("Tasks count is empty");
            return Collections.emptyList();
        }
        logger.info("Found {} tasks", tasks.getSize());
        return tasks.getContent().stream()
                .map(taskMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByAuthor(String author, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Attempting to get tasks by author with pagination...");
        Page<Task> tasks = taskRepository.findAllByAuthor(author, pageable);
        if (tasks.isEmpty()) {
            logger.info("This author does not have any tasks");
            return Collections.emptyList();
        }
        logger.info("Found {} tasks by this author", tasks.getSize());

        return tasks.getContent().stream()
                .map(taskMapper::toTaskDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByAssignee(String assignee, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        logger.info("Attempting to get tasks by assignee with pagination...");
        Page<Task> tasks = taskRepository.findAllByAssignee(assignee, pageable);
        if (tasks.isEmpty()) {
            logger.info("This assignee does not have any tasks");
            return Collections.emptyList();
        }
        logger.info("Found {} tasks for this assignee", tasks.getSize());
        return tasks.getContent().stream()
                .map(taskMapper::toTaskDTO)
                .collect(Collectors.toList());
    }
}
