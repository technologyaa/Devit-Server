package com.example.websocketchat.domain.project.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(nullable = false, length = 80)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Major major;

    @Column(nullable = false, length = 60)
    private String ownerName;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private Long creditBudget;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProjectTask> tasks = new ArrayList<>();

    public void addTask(ProjectTask task) {
        tasks.add(task);
        task.setProject(this);
    }

    public void removeTask(ProjectTask task) {
        tasks.remove(task);
        task.setProject(null);
    }

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (creditBudget == null) {
            creditBudget = 0L;
        }
        if (thumbnailUrl == null) {
            thumbnailUrl = "/assets/dummy-thumbnail.svg";
        }
    }
}

