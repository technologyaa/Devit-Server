package technologyaa.Devit.domain.developer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
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

        // 필드 업데이트
        if (request.getIntroduction() != null) {
            developer.setIntroduction(request.getIntroduction());
        }
        if (request.getCareer() != null) {
            developer.setCareer(request.getCareer());
        }
        if (request.getGithubId() != null) {
            developer.setGithubId(request.getGithubId());
        }
        if (request.getMajor() != null) {
            developer.setMajor(request.getMajor());
        }
        if (request.getBlog() != null) {
            developer.setBlog(request.getBlog());
        }

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
        DeveloperResponse response = new DeveloperResponse();
        developer.setMemberId(response.getMemberId());
        developer.setIntroduction(response.getIntroduction());
        developer.setCareer(response.getCareer());
        developer.setGithubId(response.getGithubId());
        developer.setMajor(response.getMajor());
        developer.setBlog(response.getBlog());
        developer.setTemperature(response.getTemperature());

        return response;
    }
}
