package technologyaa.Devit.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import technologyaa.Devit.domain.review.entity.Review;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ProjectId;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Major major;


    public List<Review> getParticipants() {
        return List.of();
    }

}

