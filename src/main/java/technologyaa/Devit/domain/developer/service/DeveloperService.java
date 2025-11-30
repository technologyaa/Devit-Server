package technologyaa.Devit.domain.developer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import technologyaa.Devit.domain.auth.jwt.entity.Member;
import technologyaa.Devit.domain.auth.jwt.repository.MemberRepository;
import technologyaa.Devit.domain.developer.dto.DeveloperRequest;
import technologyaa.Devit.domain.developer.dto.DeveloperResponse;
import technologyaa.Devit.domain.developer.entity.Developer;
import technologyaa.Devit.domain.developer.exception.MemberNotFoundException;
import technologyaa.Devit.domain.developer.repository.DeveloperRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeveloperService {

    private final DeveloperRepository developerRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public DeveloperResponse create(Long memberId, DeveloperRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("해당 멤버를 찾을 수 없습니다."));

        if (developerRepository.existsById(memberId)) {
            throw new IllegalArgumentException("해당 멤버의 개발자 정보가 이미 등록되어 있습니다.");

            Developer developer = Developer.builder()
                    .memberId(memberId)
                    .member(member)
                    .introduction(request.getIntroduction())
                    .career(request.getCareer())
                    .githubId(request.getGithubId())
                    .major(request.getMajor())
                    .blog(request.getBlog())
                    .temperature(request.getTemperature())
                    .build();

            return null;
        }
    }

    public List<Developer> findAll() {
        return developerRepository.findAll();
    }

    public Developer findOne(Long memberId) {
        return developerRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 ID: " + memberId + "의 개발자 정보를 찾을 수 없습니다."));
    }


    @Transactional
    public Developer update(Long memberId, DeveloperRequest request) {
        Developer developer = findOne(memberId);

        // 2. 변경 감지(Dirty Checking)를 위한 필드 값 변경
        developer.update(
                request.introduction(),
                request.career(),
                request.github(),
                request.major(),
                request.blog(),
                request.temperature()
        );

        return developer;
    }

    @Transactional
    public void delete(Long memberId) {
        if (!developerRepository.existsById(memberId)) {
            throw new IllegalArgumentException("회원 ID: " + memberId + "의 개발자 정보를 찾을 수 없으므로 삭제할 수 없습니다.");
        }
        developerRepository.deleteById(memberId);
    }


}
