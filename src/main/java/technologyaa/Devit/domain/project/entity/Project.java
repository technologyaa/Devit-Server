package technologyaa.Devit.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import technologyaa.Devit.domain.auth.jwt.entity.Member;

import java.time.LocalDateTime;
import technologyaa.Devit.domain.auth.jwt.entity.Member;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Member author;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    @Column(nullable = false)
    private Boolean isCompleted=false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createAt = LocalDateTime.now();
}
