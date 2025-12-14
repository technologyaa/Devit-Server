package technologyaa.Devit.domain.developer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.entity.Role;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.developer.dto.DeveloperRequest;
import technologyaa.Devit.domain.developer.dto.DeveloperResponse;
import technologyaa.Devit.domain.developer.entity.Developer;
import technologyaa.Devit.domain.developer.entity.Major;
import technologyaa.Devit.domain.developer.exception.DeveloperNotFoundException;
import technologyaa.Devit.domain.developer.exception.MemberNotFoundException;
import technologyaa.Devit.domain.developer.repository.DeveloperRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public DeveloperResponse createDeveloper(Long memberId, DeveloperRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 회원을 찾을 수 없습니다."));

        if (developerRepository.existsById(memberId)) {
            throw new IllegalArgumentException("해당 회원은 이미 개발자로 등록 되어있습니다.");
        }

        Developer developer = Developer.builder()
                .member(member)
                .introduction(request.getIntroduction())
                .career(request.getCareer())
                .githubId(request.getGithubId())
                .major(request.getMajor())
                .blog(request.getBlog())
                .build();

        Developer saved = developerRepository.save(developer);

        return convertToResponse(saved);
    }

    public DeveloperResponse getDeveloperById(Long memberId) {
        Developer developer = developerRepository.findById(memberId)
                .orElseThrow(() -> new DeveloperNotFoundException("해당 개발자를 찾을 수 없습니다"));

        return convertToResponse(developer);
    }

    public List<DeveloperResponse> getAllDevelopers() {
        return developerRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<DeveloperResponse> getDevelopersByMajor(Major major) {
        return developerRepository.findByMajor(major).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<DeveloperResponse> getDevelopersByCareer(Integer minCareer) {
        return developerRepository.findByCareerGreaterThanEqual(minCareer).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<DeveloperResponse> getDevelopersByTemperature(BigDecimal minTemperature) {
        return developerRepository.findByTemperatureGreaterThanEqual(minTemperature).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public DeveloperResponse updateDeveloper(Long memberId, DeveloperRequest request) {
        Developer developer = developerRepository.findById(memberId)
                .orElseThrow(() -> new DeveloperNotFoundException("해당 개발자를 찾을 수 없습니다"));

        Developer updated = developerRepository.save(developer);

        return convertToResponse(updated);
    }

    @Transactional
    public DeveloperResponse updateTemperature(Long memberId, BigDecimal temperature) {
        Developer developer = developerRepository.findById(memberId)
                .orElseThrow(() -> new DeveloperNotFoundException("해당 개발자를 찾을 수 없습니다"));

        developer.setTemperature(temperature);
        Developer updated = developerRepository.save(developer);

        return convertToResponse(updated);
    }

    @Transactional
    public void deleteDeveloper(Long memberId) {
        if (!developerRepository.existsById(memberId)) {
            throw new DeveloperNotFoundException("해당 개발자를 찾을 수 없습니다");
        }

        developerRepository.deleteById(memberId);
    }

    private DeveloperResponse convertToResponse(Developer developer) {
        return DeveloperResponse.builder()
                .memberId(developer.getMemberId())
                .introduction(developer.getIntroduction())
                .career(developer.getCareer())
                .githubId(developer.getGithubId())
                .major(developer.getMajor())
                .blog(developer.getBlog())
                .temperature(developer.getTemperature())
                .build();
    }
}
