package com.assel.school.repository;

import com.assel.school.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Шаг 5: JPA репозиторий для пользователей.
 * findByUsername() — поиск пользователя по логину (для Spring Security).
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}

