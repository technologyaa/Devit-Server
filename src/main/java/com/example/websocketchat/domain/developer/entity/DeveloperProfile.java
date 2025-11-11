package com.example.websocketchat.domain.developer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "developer_profile")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeveloperProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long profileId;

    @Column(nullable = false, length = 60)
    private String name;

    @Column(nullable = false, length = 40)
    private String job;

    @Column(nullable = false, length = 200)
    private String summary;

    @Column(nullable = false, length = 120)
    private String email;

    @Column(nullable = false)
    private String profileImageUrl;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Integer completedProjects;
}

