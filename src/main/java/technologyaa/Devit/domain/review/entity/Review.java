package technologyaa.Devit.domain.review.entity;

import jakarta.persistence.*;
import lombok.*;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.project.entity.Project;

import java.time.LocalDateTime;

@Entity
@Getter
<<<<<<< HEAD
=======
@Setter
>>>>>>> origin/review
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
<<<<<<< HEAD
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"reviewer_id", "reviewee_id", "project_id"}
        )
=======
        uniqueConstraints = @UniqueConstraint(columnNames = {"reviewer_id", "reviewee_id", "project_id"})
>>>>>>> origin/review
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User reviewer;

    @ManyToOne(optional = false)
    private User reviewee;

    @ManyToOne(optional = false)
    private Project project;

    @Column(nullable = false)
    private int rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime createdAt;

    @PrePersist
<<<<<<< HEAD
    void prePersist() {
=======
    public void prePersist() {
>>>>>>> origin/review
        this.createdAt = LocalDateTime.now();
    }
}