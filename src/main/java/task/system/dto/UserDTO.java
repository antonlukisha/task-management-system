package task.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "User DTO", description = "Data transfer object for user")
public class UserDTO {
    @Schema(description = "Identity of user")
    @Pattern(regexp = "\\d+", message = "User ID should be in correct format")
    private String id;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Email like a username")
    @Email(message = "Incorrect format email")
    private String email;

    @Schema(description = "Role of user can be ADMIN or USER")
    @Size(min = 3, max = 3, message = "Role type should be exactly 3 characters")
    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role type should be: ADMIN, USER")
    private String role;

    @Override
    public String toString() {
        return String.format("UserDTO: (id=%s, email=%s, role=%s)", id, email, role);
    }
}
