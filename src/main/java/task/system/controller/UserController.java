package task.system.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import task.system.dto.JWTRspDTO;
import task.system.dto.LoginRqsDTO;
import task.system.dto.RegisterRqsDTO;
import task.system.service.implementations.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "UserController", description = "Controller as the endpoint of the REST api for working with users")
@RequestMapping("/api/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    /**
     * METHOD POST: login.
     * This method for authentication of user.
     *
     * @param loginDTO Data transfer object for authentication.
     * @return response with status OK (200) and DTO body.
     */
    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Allow to authenticate user and get access token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully authentication"),
            }
    )
    public ResponseEntity<JWTRspDTO> login(@RequestBody LoginRqsDTO loginDTO) {
        logger.info("Received login request: {}", loginDTO.toString());
        JWTRspDTO response = userService.authenticate(loginDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * METHOD POST: register.
     * This method for registration of user.
     *
     * @param registerDTO Data transfer object for registration.
     * @return response with status CREATED (201) and DTO body.
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register user",
            description = "Allow to registration user and get access token",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Successfully registration"),
            }
    )
    public ResponseEntity<JWTRspDTO> register(@RequestBody RegisterRqsDTO registerDTO) {
        logger.info("Received registration request: {}", registerDTO.toString());
        JWTRspDTO response = userService.registration(registerDTO);
        return new ResponseEntity<>(response,  HttpStatus.CREATED);
    }

    /**
     * METHOD: refresh
     * Method for refresh of tokens by refresh token.
     *
     * @param refreshToken Refresh token for recovery of access.
     * @return response with status OK (200) and DTO body.
     */
    @Operation(
            summary = "Refresh tokens",
            description = "Allow to refresh of tokens by refresh token for access recovery.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully refresh"),
            }
    )
    @SecurityRequirement(name = "JWT")
    @PostMapping("/refresh")
    public ResponseEntity<JWTRspDTO> refreshAccessToken(@Valid @RequestParam("refresh") String refreshToken) {
        logger.info("Received refresh request: {}", refreshToken);
        JWTRspDTO response = response = userService.refresh(refreshToken);
        return new ResponseEntity<>(response,  HttpStatus.OK);
    }

}

