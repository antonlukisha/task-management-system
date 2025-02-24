package task.system.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.system.entity.Task;
import task.system.entity.User;

import java.util.List;

@Repository
@Tag(name = "TaskRepository", description = "ORM interface for working with tasks entities")
public interface TaskRepository extends JpaRepository<Task, Long> {
    Page<Task> findAllByAuthor(String author, Pageable pageable);
    Page<Task> findAllByAssignee(String assignee, Pageable pageable);
}
