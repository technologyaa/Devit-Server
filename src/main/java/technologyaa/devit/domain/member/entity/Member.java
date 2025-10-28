package technologyaa.devit.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @Builder.Default
    private Long credit = 0L;

    @Enumerated(EnumType.STRING)
    private Role role;
}
