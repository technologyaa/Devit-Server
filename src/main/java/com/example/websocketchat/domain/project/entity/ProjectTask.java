package com.example.websocketchat.domain.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long taskId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 80)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean done;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private Long creditReward;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public void markDone(boolean done) {
        this.done = done;
    }

    @PrePersist
    public void onCreate() {
        if (done == null) {
            done = false;
        }
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (creditReward == null) {
            creditReward = 1000L;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}

