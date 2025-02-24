package task.system.repository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import task.system.entity.Comment;

@Repository
@Tag(name = "CommentRepository", description = "ORM interface for working with comments entities")
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByTaskId(Long taskId, Pageable pageable);
}
