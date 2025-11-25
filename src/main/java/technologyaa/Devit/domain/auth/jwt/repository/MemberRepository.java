package technologyaa.Devit.domain.auth.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.auth.jwt.entity.Member;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);
}
