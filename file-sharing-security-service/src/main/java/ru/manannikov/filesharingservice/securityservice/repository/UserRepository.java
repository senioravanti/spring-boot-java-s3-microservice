package ru.manannikov.filesharingservice.securityservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.manannikov.filesharingservice.securityservice.models.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String email);
    boolean existsByUsername(String email);
}
