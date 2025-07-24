package com.shcho.shBlog.user.repository;

import com.shcho.shBlog.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
