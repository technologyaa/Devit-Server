package technologyaa.Devit.domain.developer.controller;

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

@RestController
@RequestMapping("/developers")
@RequiredArgsConstructor
public class DeveloperController {

    private final DeveloperService developerService;

    /**
     * 개발자 등록
     * POST /api/developers/{memberId}
     */
    @PostMapping("/{memberId}")
    public ResponseEntity<DeveloperResponse> createDeveloper(
            @PathVariable Long memberId,
            @Valid @RequestBody DeveloperRequest request) {
        DeveloperResponse response = developerService.createDeveloper(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // find one
    @GetMapping("/{memberId}")
    public ResponseEntity<DeveloperResponse> getDeveloper(@PathVariable Long memberId) {
        DeveloperResponse response = developerService.getDeveloperById(memberId);
        return ResponseEntity.ok(response);
    }

    // find all
    @GetMapping
    public ResponseEntity<List<DeveloperResponse>> getAllDevelopers() {
        List<DeveloperResponse> responses = developerService.getAllDevelopers();
        return ResponseEntity.ok(responses);
    }

    // 전공 별 조회
    @GetMapping("/major/{major}")
    public ResponseEntity<List<DeveloperResponse>> getDevelopersByMajor(
            @PathVariable Major major) {
        List<DeveloperResponse> responses = developerService.getDevelopersByMajor(major);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/temperature")
    public ResponseEntity<List<DeveloperResponse>> getDevelopersByTemperature(
            @RequestParam(name = "min", defaultValue = "30.0") BigDecimal minTemperature) {
        List<DeveloperResponse> responses = developerService.getDevelopersByTemperature(minTemperature);
        return ResponseEntity.ok(responses);
    }

    // update
    @PutMapping("/{memberId}")
    public ResponseEntity<DeveloperResponse> updateDeveloper(
            @PathVariable Long memberId,
            @Valid @RequestBody DeveloperRequest request) {
        DeveloperResponse response = developerService.updateDeveloper(memberId, request);
        return ResponseEntity.ok(response);
    }


    // delete
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteDeveloper(@PathVariable Long memberId) {
        developerService.deleteDeveloper(memberId);
        return ResponseEntity.noContent().build();
    }

}
