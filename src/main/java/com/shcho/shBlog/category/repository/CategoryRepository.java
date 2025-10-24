package com.shcho.shBlog.category.repository;

import com.shcho.shBlog.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findAllByUser_UserIdOrderByNameAsc(Long userId);

    boolean existsByUser_UserIdAndName(Long userId, String name);
}
