package technologyaa.devit.domain.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.devit.domain.member.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);
}
