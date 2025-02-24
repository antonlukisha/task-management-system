package task.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "comments")
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text", nullable = false)
    private String text;

    @Column(name = "author", nullable = false)
    private String author;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}

