package technologyaa.Devit.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
<<<<<<< HEAD
import technologyaa.Devit.domain.auth.oauth.entity.User;

import java.util.HashSet;
import java.util.Set;
=======
import technologyaa.Devit.domain.review.entity.Review;

import java.util.List;
>>>>>>> origin/review

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "project_id")
    private Long projectId;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Major major;
<<<<<<< HEAD
=======


    public List<Review> getParticipants() {
        return List.of();
    }

}
>>>>>>> origin/review

    @Column(length = 500)
    private String profile;


    @ManyToMany
    @JoinTable(
            name = "project_participants",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> participants = new HashSet<>();
}