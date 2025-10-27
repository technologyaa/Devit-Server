package technologyaa.devit.domain.member.oatuh.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import technologyaa.devit.domain.member.oatuh.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
