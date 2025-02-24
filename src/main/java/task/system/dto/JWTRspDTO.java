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
@Schema(title = "Jwt response DTO", description = "Data transfer object for jwt response")
public class JWTRspDTO {
    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Access token")
    private String accessToken;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Refresh token")
    private String refreshToken;

    @Schema(description = "Identity of user")
    @Pattern(regexp = "\\d+", message = "User ID should be in correct format")
    private String id;

    @NotBlank(message = "Field cannot be empty")
    @Schema(description = "Email like a username")
    @Email(message = "Incorrect format email")
    private String email;

    @Schema(description = "Role of user can be ADMIN or USER")
    @Size(min = 4, max = 5, message = "Role type should be from 4 to 5 characters")
    @Pattern(regexp = "^(ADMIN|USER)$", message = "Role type should be: ADMIN, USER")
    private String role;

    @Override
    public String toString() {
        return String.format("JWTRspDTO: (accessToken=%s, refreshToken=%s, id=%s, email=%s, role=%s)",
                accessToken, refreshToken, id, email, role);
    }
}
