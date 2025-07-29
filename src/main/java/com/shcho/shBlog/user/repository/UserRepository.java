package com.shcho.shBlog.user.repository;

import com.shcho.shBlog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    User getReferenceByUsername(String username);

    Optional<User> findByUsername(String username);
}
