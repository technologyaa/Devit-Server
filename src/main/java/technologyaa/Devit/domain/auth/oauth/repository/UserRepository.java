package technologyaa.Devit.domain.auth.oauth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import technologyaa.Devit.domain.auth.oauth.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByEmailAndProvider(String email, String provider);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.provider = :provider")
    Optional<User> findUserByEmailAndProvider(@Param("email") String email, @Param("provider") String provider);
    
    boolean existsByEmail(String email);
    
    boolean existsByEmailAndProvider(String email, String provider);
    Optional<User> findByUsername(String username);
}

