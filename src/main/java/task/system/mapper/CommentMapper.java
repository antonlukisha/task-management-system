package task.system.mapper;

import org.springframework.stereotype.Component;
import task.system.dto.CommentDTO;
import task.system.entity.Comment;
import task.system.entity.Task;

@Component
public class CommentMapper {
    public CommentDTO toCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .text(comment.getText())
                .author(comment.getAuthor())
                .build();
    }

    public Comment toCommentEntity(CommentDTO commentDTO, Task task) {
        return Comment.builder()
                .text(commentDTO.getText())
                .task(task)
                .author(commentDTO.getAuthor())
                .build();
    }
}
