package task.system.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.system.entity.User;

import java.util.Optional;

@Repository
@Tag(name = "UserRepository", description = "ORM interface for working with users entities")
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
