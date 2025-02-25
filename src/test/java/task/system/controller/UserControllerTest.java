package task.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import task.system.dto.JWTRspDTO;
import task.system.dto.LoginRqsDTO;
import task.system.dto.RegisterRqsDTO;
import task.system.service.implementations.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    private MockMvc mock;
    @InjectMocks
    private UserController userController;
    @Mock
    private UserService userService;

    private JWTRspDTO jwtRspDTO;
    @BeforeEach
    void setUp() {
        jwtRspDTO = JWTRspDTO.builder()
                .accessToken("ACCESS")
                .refreshToken("REFRESH")
                .email("example@mail.com")
                .build();
        mock = MockMvcBuilders.standaloneSetup(userController).build(); }

    @Test
    void login_Ok() throws Exception {
        LoginRqsDTO loginDTO = new LoginRqsDTO();
        loginDTO.setEmail("example@mail.com");
        loginDTO.setPassword("password");

        JWTRspDTO response = jwtRspDTO;
        when(userService.authenticate(any(LoginRqsDTO.class))).thenReturn(response);

        mock.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(loginDTO)))
                .andExpect(jsonPath("$.accessToken").value("ACCESS"))
                .andExpect(jsonPath("$.refreshToken").value("REFRESH"))
                .andExpect(jsonPath("$.email").value("example@mail.com"))
                .andExpect(status().isOk());
    }

    @Test
    void register_Created() throws Exception {
        RegisterRqsDTO registerDTO = new RegisterRqsDTO();
        registerDTO.setEmail("example@mail.com");
        registerDTO.setPassword("password");
        registerDTO.setRole("USER");

        JWTRspDTO response = jwtRspDTO;
        when(userService.registration(any(RegisterRqsDTO.class))).thenReturn(response);

        mock.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(registerDTO)))
                .andExpect(jsonPath("$.accessToken").value("ACCESS"))
                .andExpect(jsonPath("$.refreshToken").value("REFRESH"))
                .andExpect(jsonPath("$.email").value("example@mail.com"))
                .andExpect(status().isCreated());
    }

    @Test
    void refreshAccessToken_Ok() throws Exception {
        JWTRspDTO response = jwtRspDTO;
        when(userService.refresh(any(String.class))).thenReturn(response);

        mock.perform(post("/api/auth/refresh")
                        .param("refresh", "ANY_TOKEN"))
                .andExpect(jsonPath("$.accessToken").value("ACCESS"))
                .andExpect(jsonPath("$.refreshToken").value("REFRESH"))
                .andExpect(jsonPath("$.email").value("example@mail.com"))
                .andExpect(status().isOk());
    }
}
