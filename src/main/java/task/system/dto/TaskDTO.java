package task.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "Task DTO", description = "Data transfer object for task")
public class TaskDTO {
    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Title of task")
    @Size(min = 3, max = 100, message = "Title size must be from 3 to 100 characters")
    private String title;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Description of task")
    @Size(max = 500, message = "Description is too long")
    private String description;

    @Schema(description = "Status of task")
    @Pattern(regexp = "^(PENDING|IN_PROGRESS|COMPLETED)$", message = "Status type should be: PENDING, IN_PROGRESS, COMPLETED")
    private String status;

    @Schema(description = "Task priority")
    @Pattern(regexp = "^(HIGH|MEDIUM|LOW)$", message = "Priority type should be: HIGH, MEDIUM, LOW")
    private String priority;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Author of task")
    private String author;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Assignee of task")
    private String assignee;

    @Override
    public String toString() {
        return String.format("TaskDTO: (title=%s, description=%s, status=%s, priority=%s, author=%s, assignee=%s)",
                title, description, status, priority, author, assignee);
    }
}

