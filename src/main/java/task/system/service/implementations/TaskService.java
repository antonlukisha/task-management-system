package task.system.service.implementations;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.system.dto.TaskDTO;
import task.system.entity.Task;
import task.system.exception.implementations.TaskException;
import task.system.mapper.TaskMapper;
import task.system.repository.TaskRepository;
import task.system.service.TaskServiceInterface;

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
    public Long createTask(TaskDTO taskDTO) {
        logger.info("Creating new task...");
        logger.info("Fetching username from Security Context...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        if (currentRole.equals("ROLE_USER")) {
            logger.error("User with role {} is not authorized to create task", currentRole);
            throw TaskException.of(HttpStatus.FORBIDDEN, "Incorrect role for current operation");
        }
        try {
            Task task = taskMapper.toTaskEntity(taskDTO);
            taskRepository.save(task);
            logger.info("Task successfully created with ID: {}", task.getId());
            return task.getId();
        } catch (Exception exception) {
            logger.error("Failed creating task: ", exception);
            throw TaskException.of(HttpStatus.BAD_REQUEST, "Failed creating task");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) throws TaskException {
        logger.info("Fetching task with ID: {}...", id);
        logger.info("Fetching username from Security Context...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            logger.error("User is not authorized to fetch task");
            throw TaskException.of(HttpStatus.FORBIDDEN, "Incorrect role for current operation");
        }
        String currentRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        Task task = taskDataService.getTaskFromCacheOrDatabase(id);
        return taskMapper.toTaskDTO(task);
    }

    @Override
    @Transactional
    public boolean deleteTask(Long taskId) throws TaskException {
        logger.info("Deleting task by ID: {}...", taskId);
        logger.info("Fetching username from Security Context...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        if (currentRole.equals("ROLE_USER")) {
            logger.error("User with role {} is not authorized to delete task", currentRole);
            throw TaskException.of(HttpStatus.FORBIDDEN, "Incorrect role for current operation");
        }
        try {
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
    public boolean updateTaskById(Long taskId, TaskDTO taskDTO) throws TaskException {
        logger.info("Updating task with ID: {}...", taskId);
        logger.info("Fetching username from Security Context...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        String currentUsername = authentication.getName();
        if (currentRole.equals("ROLE_USER") && !currentUsername.equals(taskDTO.getAssignee())) {
            logger.error("User with role {} is not authorized to update task", currentRole);
            throw TaskException.of(HttpStatus.FORBIDDEN, "Incorrect role for current operation");
        }
        try {
            Optional<Task> taskOptional = taskRepository.findById(taskId);
            if (taskOptional.isPresent()) {
                Task task = taskMapper.toTaskEntity(taskDTO);
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
    public boolean assignTaskToUser(Long taskId, String assignee) throws TaskException {
        logger.info("Assigning task by ID: {} to user: {}...", taskId, assignee);
        logger.info("Fetching username from Security Context...");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentRole = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("ROLE_USER");
        if (currentRole.equals("ROLE_USER")) {
            logger.error("User with role {} is not authorized to assign user to task", currentRole);
            throw TaskException.of(HttpStatus.FORBIDDEN, "Incorrect role for assigning user to task");
        }
        try {
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
