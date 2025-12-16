package technologyaa.Devit.domain.project.entity;

import jakarta.persistence.*;
import lombok.*;
import technologyaa.Devit.domain.auth.oauth.entity.User;

import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    private String projectName;

    @OneToMany(mappedBy = "project")
    private List<ProjectParticipant> participants;

    /** 참여자 리스트(User) 반환 */
    public List<User> getParticipants() {
        return participants.stream()
                .map(ProjectParticipant::getUser)
                .collect(Collectors.toList());
    }
}