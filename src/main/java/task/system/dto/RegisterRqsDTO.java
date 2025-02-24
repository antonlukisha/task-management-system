package task.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
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
@Schema(title = "Registration request DTO", description = "Data transfer object for registration request")
public class RegisterRqsDTO {
    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Email like a username")
    @Email(message = "Incorrect format email")
    private String email;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Password")
    @Size(min = 6, max = 30, message = "Password should be from 6 to 30 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password should contain at least one letter, one number, and one special character.")
    private String password;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Role of user can be ADMIN or USER")
    @Size(min = 3, max = 3, message = "Role type should be exactly 3 characters")
    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role type should be: ADMIN, USER")
    private String role;

    @Override
    public String toString() {
        return String.format("RegisterRqsDTO: (email=%s, password=%s, role=%s)",
                email, password, role);
    }
}
