package com.example.websocketchat.domain.home.controller;

import com.example.websocketchat.domain.home.dto.QuickLinkResponse;
import com.example.websocketchat.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "홈", description = "홈 화면 구성 요소 API")
@RestController
@RequestMapping("/api/home")
public class HomeContentController {

    @Operation(summary = "바로가기 카드 조회", description = "홈 화면의 바로가기 카드 정보를 제공합니다.")
    @GetMapping("/quick-links")
    public ResponseEntity<ApiResponse<List<QuickLinkResponse>>> getQuickLinks() {
        List<QuickLinkResponse> response = List.of(
                QuickLinkResponse.builder()
                        .url("/projects")
                        .logo("/assets/white_folder.svg")
                        .alt("폴더 아이콘")
                        .text("새로운 프로젝트 시작")
                        .name("프로젝트")
                        .gradient("linear-gradient(270deg, #9636EB 0.15%, #A651F5 99.87%)")
                        .build(),
                QuickLinkResponse.builder()
                        .url("/offer/dev")
                        .logo("/assets/white-people.svg")
                        .alt("개발자 아이콘")
                        .text("개발자찾기")
                        .name("개발자")
                        .gradient("linear-gradient(90deg, #C083FC 0%, #AA57F7 100%)")
                        .build(),
                QuickLinkResponse.builder()
                        .url("/shop")
                        .logo("/assets/white-shop.svg")
                        .alt("상점 아이콘")
                        .text("코인 & 유료플랜")
                        .name("상점")
                        .gradient("linear-gradient(90deg, #D6B0FF 0%, #BF86FC 100%)")
                        .build()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, HttpStatus.OK.value(), "홈 바로가기 정보를 조회했습니다.", response)
        );
    }
}

