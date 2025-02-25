package task.system.service.implementations;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import task.system.entity.User;
import task.system.exception.implementations.UserException;
import task.system.repository.UserRepository;

@Tag(name = "UserDetailsServiceImpl", description = "Service which implement UserDetailsService")
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UserException {
        try {
            User user = userRepository.findByEmail(username)
                    .orElseThrow(() -> UserException.of(HttpStatus.NOT_FOUND, "User not found"));
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    user.getAuthorities());
        } catch (UserException exception) {
            throw UserException.of(HttpStatus.NOT_FOUND, "User not found");
        }
    }
}
