package technologyaa.Devit.domain.developer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import technologyaa.Devit.domain.developer.dto.DeveloperRequest;
import technologyaa.Devit.domain.developer.dto.DeveloperResponse;
import technologyaa.Devit.domain.developer.entity.Major;
import technologyaa.Devit.domain.developer.service.DeveloperService;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "개발자 (Developer)", description = "개발자 관리 API")
@RestController
@RequestMapping("/developers")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    /**
     * 개발자 등록
     * POST /api/developers/{memberId}
     */
    @Operation(summary = "개발자 생성", description = "memberId를 통해 새 개발자를 만듭니다.")
    @PostMapping("/{memberId}")
    public ResponseEntity<DeveloperResponse> createDeveloper(
            @PathVariable Long memberId,
            @Valid @RequestBody DeveloperRequest request) {
        DeveloperResponse response = developerService.createDeveloper(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // find one
    @Operation(summary = "개발자 ID로 조회", description = "memberId를 사용해 개발자를 조회합니다.")
    @GetMapping("/{memberId}")
    public ResponseEntity<DeveloperResponse> getDeveloper(@PathVariable Long memberId) {
        DeveloperResponse response = developerService.getDeveloperById(memberId);
        return ResponseEntity.ok(response);
    }

    // find all
    @Operation(summary = "모든 개발자 조회", description = "모든 개발자를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<DeveloperResponse>> getAllDevelopers() {
        List<DeveloperResponse> responses = developerService.getAllDevelopers();
        return ResponseEntity.ok(responses);
    }

    // 전공 별 조회
    @Operation(summary = "개발자 전공 별로 조회", description = "특정 전공의 개발자를 조회합니다.")
    @GetMapping("/major/{major}")
    public ResponseEntity<List<DeveloperResponse>> getDevelopersByMajor(
            @PathVariable Major major) {
        List<DeveloperResponse> responses = developerService.getDevelopersByMajor(major);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "개발자 온도 별로 조회", description = "최소 온도 이상의 개발자를 조회합니다.")
    @GetMapping("/temperature")
    public ResponseEntity<List<DeveloperResponse>> getDevelopersByTemperature(
            @RequestParam(name = "min", defaultValue = "30.0") BigDecimal minTemperature) {
        List<DeveloperResponse> responses = developerService.getDevelopersByTemperature(minTemperature);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "memberId로 업데이트", description = "memberId를 통해 개발자 정보를 수정합니다.")
    // update
    @PutMapping("/{memberId}")
    public ResponseEntity<DeveloperResponse> updateDeveloper(
            @PathVariable Long memberId,
            @Valid @RequestBody DeveloperRequest request) {
        DeveloperResponse response = developerService.updateDeveloper(memberId, request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "memberId로 삭제", description = "memberId를 통해 개발자 정보를 삭제합니다.")
    // delete
    @DeleteMapping("/{memberId}")
    public void deleteDeveloper(@PathVariable Long memberId) {
        developerService.deleteDeveloper(memberId);
    }
}
