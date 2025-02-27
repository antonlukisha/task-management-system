package task.system.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import task.system.dto.JWTRspDTO;
import task.system.dto.LoginRqsDTO;
import task.system.dto.RegisterRqsDTO;
import task.system.entity.User;
import task.system.exception.implementations.UserException;
import task.system.repository.UserRepository;
import task.system.security.JwtTokenUtil;
import task.system.type.Role;
import task.system.service.implementations.UserService;

import java.util.ArrayList;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private Authentication authentication;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private RegisterRqsDTO registerAdminDTO;
    private RegisterRqsDTO registerUserDTO;
    private LoginRqsDTO loginWrongDTO;
    private LoginRqsDTO loginCorrectDTO;
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .email("example@mail.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();
        registerAdminDTO = RegisterRqsDTO.builder()
                .email("example@mail.com")
                .password("password")
                .role("ADMIN")
                .build();
        registerUserDTO = RegisterRqsDTO.builder()
                .email("example@mail.com")
                .password("password")
                .role("USER")
                .build();
        loginCorrectDTO = LoginRqsDTO.builder()
                .email("example@mail.com")
                .password("password")
                .build();
        loginWrongDTO = LoginRqsDTO.builder()
                .email("example@mail.com")
                .password("wrongPassword")
                .build();
    }

    @Test
    void registration_Success() {
        when(userRepository.findByEmail(registerAdminDTO.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerAdminDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtTokenUtil.generateAccessToken(any(User.class))).thenReturn("ACCESS");
        when(jwtTokenUtil.generateRefreshToken(any(User.class))).thenReturn("REFRESH");
        JWTRspDTO result = userService.registration(registerAdminDTO);
        assertNotNull(result);
        assertEquals("ACCESS", result.getAccessToken());
        assertEquals("REFRESH", result.getRefreshToken());
        verify(jwtTokenUtil, times(1)).generateAccessToken(any(User.class));
        SecurityContextHolder.clearContext();
    }

    @Test
    void registration_Failure() {
        when(userRepository.findByEmail(registerUserDTO.getEmail())).thenReturn(Optional.of(user));
        UserException exception = assertThrows(UserException.class, () -> userService.registration(registerUserDTO));
        assertEquals(HttpStatus.BAD_REQUEST, exception.getCode());
    }

    @Test
    void login_Success() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new org.springframework.security.core.userdetails.User(loginCorrectDTO.getEmail(), loginCorrectDTO.getPassword(), new ArrayList<>()));
        when(jwtTokenUtil.generateAccessToken(any())).thenReturn("ACCESS");
        when(jwtTokenUtil.generateRefreshToken(any())).thenReturn("REFRESH");
        JWTRspDTO result = userService.authenticate(loginCorrectDTO);
        assertNotNull(result);
        assertEquals("ACCESS", result.getAccessToken());
        assertEquals("REFRESH", result.getRefreshToken());
        SecurityContextHolder.clearContext();
    }

    @Test
    void login_Failure() {
        when(authenticationManager.authenticate(any())).thenThrow(new AuthenticationException("Invalid credentials") {});
        UserException exception = assertThrows(UserException.class, () -> userService.authenticate(loginWrongDTO));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getCode());
    }
}
