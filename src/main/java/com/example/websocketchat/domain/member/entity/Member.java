package com.example.websocketchat.domain.member.entity;

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
    private Long credit;

    @Enumerated(EnumType.STRING)
    private Role role;
}

