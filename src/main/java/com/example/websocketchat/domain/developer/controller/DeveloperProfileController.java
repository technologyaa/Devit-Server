package com.example.websocketchat.domain.developer.controller;

import com.example.websocketchat.domain.developer.dto.DeveloperSummaryResponse;
import com.example.websocketchat.domain.developer.service.DeveloperProfileService;
import com.example.websocketchat.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "개발자 추천", description = "홈 화면 및 개발자 추천 섹션에 사용되는 API")
@RestController
@RequestMapping("/api/developers")
@RequiredArgsConstructor
public class DeveloperProfileController {

    private final DeveloperProfileService developerProfileService;

    @Operation(summary = "추천 개발자 조회", description = "프론트엔드 홈 화면에 필요한 추천 개발자 목록을 반환합니다.")
    @GetMapping("/recommended")
    public ResponseEntity<ApiResponse<List<DeveloperSummaryResponse>>> getRecommendedDevelopers() {
        List<DeveloperSummaryResponse> response = developerProfileService.getRecommendedDevelopers();
        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "추천 개발자 목록을 조회했습니다.", response)
        );
    }
}

