package com.example.websocketchat.domain.developer.repository;

import com.example.websocketchat.domain.developer.entity.DeveloperProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperProfileRepository extends JpaRepository<DeveloperProfile, Long> {
}

