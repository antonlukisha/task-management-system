package task.system.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import task.system.security.JwtAuthenticationFilter;
import task.system.service.implementations.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public String[] PERMIT_ALL = {
            "/swagger*/**",
            "/swagger-ui/**",
            "/backend/swagger-ui.html",
            "/documentation/**",
            "/v3/api-docs/**",
            "/api/login",
            "/api/register"
    };

    public String[] ANOTHER_PATHS = {
            "/api/tasks/**",
            "/api/tasks/comments/**",
    };

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((auth) -> {
            auth.requestMatchers(PERMIT_ALL).permitAll();
            auth.requestMatchers(ANOTHER_PATHS).hasRole("USER");
            auth.requestMatchers(ANOTHER_PATHS).hasRole("ADMIN");
            auth.anyRequest().authenticated();
        })
                .sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }
}


