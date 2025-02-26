package task.system.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import task.system.type.Priority;
import task.system.type.Status;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Schema(title = "Task Entity", description = "Task entity representing a task in the system with serialization")
@Table(name = "tasks")
public class Task implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status; // PENDING, IN_PROGRESS, COMPLETED

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority; // HIGH, MEDIUM, LOW

    @Column(name = "author", nullable = false)
    private String author;

    @Column(name = "assignee", nullable = false)
    private String assignee;

    @OneToMany(mappedBy = "task", fetch = FetchType.EAGER)
    private List<Comment> comments;
}
