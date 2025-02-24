package task.system.service;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import task.system.dto.JWTRspDTO;
import task.system.dto.LoginRqsDTO;
import task.system.dto.RegisterRqsDTO;

@Tag(name = "UserServiceInterface", description = "Interface of users service")
public interface UserServiceInterface {
    /**
     * METHOD: registration
     * Method for registering a new user.
     *
     * @param request Data required for user registration (e.g., username, email, password).
     * @return {@link JWTRspDTO} - Response containing the JWT token for the registered user.
     */
    @Operation(
            summary = "User registration",
            description = "Registers a new user and returns a dto with JWT tokens for the user."
    )
    JWTRspDTO registration(RegisterRqsDTO request);

    /**
     * METHOD: authenticate
     * Method for authenticating a user.
     *
     * @param request Data required for user login (e.g., email and password).
     * @return {@link JWTRspDTO} - Response containing the JWT token for the authenticated user.
     */
    @Operation(
            summary = "User authentication",
            description = "Authenticates a user with email and password, and returns dto with JWT tokens."
    )
    JWTRspDTO authenticate(LoginRqsDTO request);

    /**
     * METHOD: refresh
     * Method for refresh of tokens by refresh token.
     *
     * @param token Refresh token for recovery of access.
     * @return {@link JWTRspDTO} - Response containing the JWT token for access.
     */
    @Operation(
            summary = "Refresh tokens",
            description = "Refresh of tokens by refresh token for recovery of access."
    )
    JWTRspDTO refresh(String token);
}
