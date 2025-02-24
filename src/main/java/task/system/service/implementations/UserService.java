package task.system.service.implementations;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import task.system.dto.JWTRspDTO;
import task.system.dto.LoginRqsDTO;
import task.system.dto.RegisterRqsDTO;
import task.system.entity.User;
import task.system.exception.implementations.UserException;
import task.system.repository.UserRepository;
import task.system.security.JwtTokenUtil;
import task.system.service.UserServiceInterface;
import task.system.type.Role;

import java.util.ArrayList;

@Service
@Tag(name = "UserService", description = "Service with business logic for working with users")
@RequiredArgsConstructor
public class UserService implements UserServiceInterface {
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Override
    @Transactional
    public JWTRspDTO registration(RegisterRqsDTO registerDTO) {
        logger.info("Starting registration process for DTO: {} ...", registerDTO.toString());
        if(userRepository.findByEmail(registerDTO.getEmail()).isPresent()){
            logger.error("User with email {} already exists", registerDTO.getEmail());
            throw UserException.of(HttpStatus.BAD_REQUEST, "User with this email is already exist");
        }

        String encodePassword = passwordEncoder.encode(registerDTO.getPassword());
        User user = User.builder()
                .email(registerDTO.getEmail())
                .password(encodePassword)
                .role(Role.valueOf(registerDTO.getRole()))
                .build();
        userRepository.save(user);
        logger.info("User with email {} registered successfully. Generating refresh and access tokens...", registerDTO.getEmail());
        String token = jwtTokenUtil.generateAccessToken(user);
        String refreshToken = jwtTokenUtil.generateRefreshToken(user);
        return JWTRspDTO.builder()
                .accessToken(token)
                .refreshToken(refreshToken)
                .id(user.getId().toString())
                .email(user.getEmail())
                .role(String.valueOf(user.getRole()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public JWTRspDTO authenticate(LoginRqsDTO loginDTO) {
        logger.info("Attempting to authenticate user for DTO: {}...", loginDTO.toString());
        try {
            Authentication authenticate = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword())
            );
            User user = (User) authenticate.getPrincipal();
            logger.info("User with email {} authenticated successfully. Generating new refresh and access tokens...", loginDTO.getEmail());
            String token = jwtTokenUtil.generateAccessToken(user);
            String refreshToken = jwtTokenUtil.generateRefreshToken(user);
            String role = user.getRole().name();
            return JWTRspDTO.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .id(user.getId().toString())
                    .email(user.getEmail())
                    .role(role)
                    .build();
        } catch (AuthenticationException exception) {
            logger.error("Authentication failed for email {}: {}", loginDTO.getEmail(), exception.getMessage());
            throw UserException.of(HttpStatus.UNAUTHORIZED, "Incorrect login or password");
        }
    }

    @Override
    @Transactional
    public JWTRspDTO refresh(String token) {
        return jwtTokenUtil.refreshAccessToken(token);
    }
}
