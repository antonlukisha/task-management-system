package task.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "Comment DTO", description = "Data transfer object for comment")
public class CommentDTO {
    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Text of comment")
    @Size(min = 1, max = 100, message = "Comment size must be from 1 to 100 characters")
    private String text;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Email of task author")
    @Email(message = "Incorrect format email")
    private String author;

    @Override
    public String toString() {
        return String.format("CommentDTO: (text=%s, author=%s)",
                text, author);
    }
}
