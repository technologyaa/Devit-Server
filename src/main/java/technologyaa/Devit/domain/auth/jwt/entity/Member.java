package technologyaa.Devit.domain.auth.jwt.entity;

import jakarta.persistence.*;
import lombok.*;
import technologyaa.Devit.domain.developer.entity.Developer;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String username;
    private String password;
    @Column(unique = true)
    private String email;

    private String createdAt;
    private Long credit;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Developer developer;
}

