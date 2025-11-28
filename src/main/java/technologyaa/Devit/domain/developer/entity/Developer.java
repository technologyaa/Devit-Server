package technologyaa.Devit.domain.developer.entity;

import jakarta.persistence.*;
import lombok.*;
import technologyaa.Devit.domain.auth.jwt.entity.Member;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Developer {

    @Id
    private Long memberId;

    @OneToOne
    @MapsId
    private Member member;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    private Integer career;

    @Column(length = 50)
    private String githubId;

    @Enumerated(EnumType.STRING)
    private Major major;

    @Column(length = 255)
    private String blog;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperature;
}
