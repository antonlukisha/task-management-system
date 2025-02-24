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
@Schema(title = "Login request DTO", description = "Data transfer object for login request")
public class LoginRqsDTO {
    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Email like a username")
    @Email(message = "Incorrect format email")
    private String email;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Password")
    @Size(min = 6, max = 30, message = "Password should be from 6 to 30 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password should contain at least one letter, one number, and one special character.")
    private String password;

    @Override
    public String toString() {
        return String.format("LoginRqsDTO: (email=%s, password=%s)",
                email, password);
    }
}
