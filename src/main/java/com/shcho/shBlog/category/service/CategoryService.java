package com.shcho.shBlog.category.service;

import com.shcho.shBlog.category.dto.CreateCategoryRequestDto;
import com.shcho.shBlog.category.entity.Category;
import com.shcho.shBlog.category.repository.CategoryRepository;
import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.shcho.shBlog.libs.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category createCategory(User user, CreateCategoryRequestDto requestDto) {

        if(categoryRepository.existsByUser_UserIdAndName(user.getUserId(), requestDto.name())){
            throw new CustomException(DUPLICATE_CATEGORY_NAME);
        }

        Category category = Category.of(user, requestDto.name(), requestDto.description());
        categoryRepository.save(category);
        return category;
    }

    public List<Category> getAllCategories(User user) {
        Long userId = user.getUserId();
        return categoryRepository.findAllByUser_UserIdOrderByNameAsc(userId);
    }

    @Transactional
    public void deleteCategoryByCategoryId(User user, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));

        if (!category.getUser().getUserId().equals(user.getUserId())) {
            throw new CustomException(FORBIDDEN_CATEGORY_DELETE);
        }

        categoryRepository.delete(category);
    }
}
