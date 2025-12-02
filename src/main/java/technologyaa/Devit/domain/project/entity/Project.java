package technologyaa.Devit.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
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
    @Column(name = "project_id")
    private Long projectId;

    @Column(nullable = false, length = 40)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    private Major major;

    @Column(length = 500)
    private String profile;
}

