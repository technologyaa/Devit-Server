package technologyaa.Devit.domain.auth.jwt.entity;

import jakarta.persistence.*;
import lombok.*;
import technologyaa.Devit.domain.project.entity.Project;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;

    private String createdAt;
    private Long credit;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean isDeveloper;

    @Column(length = 500)
    private String profile;

    @ManyToMany(mappedBy = "members")
    @Builder.Default
    private Set<Project> projects = new HashSet<>();
}

