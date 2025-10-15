package com.example.websocketchat.domain.project.dto;

import lombok.Getter;
import lombok.Setter;
import com.example.websocketchat.domain.project.entity.Major;

@Getter
@Setter
public class ProjectRequest {
    private String title;
    private String content;
    private Major major;
}

