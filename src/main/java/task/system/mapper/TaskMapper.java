package task.system.mapper;

import org.springframework.stereotype.Component;
import task.system.dto.TaskDTO;
import task.system.entity.Task;
import task.system.type.Priority;
import task.system.type.Status;

@Component
public class TaskMapper {
    public TaskDTO toTaskDTO(Task task) {
        return TaskDTO.builder()
                .title(task.getTitle())
                .description(task.getDescription())
                .author(task.getAuthor())
                .assignee(task.getAssignee())
                .status(String.valueOf(task.getStatus()))
                .priority(String.valueOf(task.getPriority()))
                .build();
    }

    public Task toTaskEntity(TaskDTO taskDTO) {
        return Task.builder()
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .author(taskDTO.getAuthor())
                .assignee(taskDTO.getAssignee())
                .status(Status.valueOf(taskDTO.getStatus()))
                .priority(Priority.valueOf(taskDTO.getPriority()))
                .build();
    }
}
