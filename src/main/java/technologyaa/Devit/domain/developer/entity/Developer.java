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

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    private Member member;

    @Column(columnDefinition = "TEXT")
    private String introduction;

    private Integer career;

    @Column(length = 50)
    private String githubId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Major major;

    @Column(length = 255)
    private String blog;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperature;

    @PrePersist
    public void prePersist() {
        this.temperature = new BigDecimal(36.5);
    }
}
