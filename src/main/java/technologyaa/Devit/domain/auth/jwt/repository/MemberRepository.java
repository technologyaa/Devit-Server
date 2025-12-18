package technologyaa.Devit.domain.auth.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.Devit.domain.auth.jwt.entity.Member;

import java.util.Optional;

import technologyaa.Devit.domain.auth.jwt.entity.Role;
import java.util.List;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
    
    List<Member> findByRole(Role role);
}