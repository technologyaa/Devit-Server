package technologyaa.Devit.domain.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.oauth.entity.User;
import technologyaa.Devit.domain.project.entity.Project;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"sender_id", "receiver_id", "project_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Member sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false)
    private byte rating;

    @Column(columnDefinition = "TEXT")
    private String content;

    public void update(byte rating, String content) {
        this.rating = rating;
        this.content = content;
    }
}
