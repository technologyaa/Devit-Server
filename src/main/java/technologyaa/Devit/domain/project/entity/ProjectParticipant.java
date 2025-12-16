package technologyaa.Devit.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import technologyaa.Devit.domain.auth.oauth.entity.User;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Project project;

    @ManyToOne(optional = false)
    private User user;
}