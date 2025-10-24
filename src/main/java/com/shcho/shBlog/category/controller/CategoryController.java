package com.shcho.shBlog.category.controller;

import com.shcho.shBlog.auth.CustomUserDetails;
import com.shcho.shBlog.category.dto.CategoryResponseDto;
import com.shcho.shBlog.category.dto.CreateCategoryRequestDto;
import com.shcho.shBlog.category.dto.CreateCategoryResponseDto;
import com.shcho.shBlog.category.entity.Category;
import com.shcho.shBlog.category.service.CategoryService;
import com.shcho.shBlog.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CreateCategoryResponseDto> createCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody CreateCategoryRequestDto requestDto
    ) {
        User user = userDetails.getUser();
        Category newCategory = categoryService.createCategory(user, requestDto);

        return ResponseEntity.ok(CreateCategoryResponseDto.from(user.getUsername(), newCategory));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAllCategories(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        List<CategoryResponseDto> categories = categoryService.getAllCategories(user)
                .stream()
                .map(CategoryResponseDto::from)
                .toList();

        return ResponseEntity.ok(categories);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<String> deleteCategory(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long categoryId
    ) {
        User user = userDetails.getUser();
        categoryService.deleteCategoryByCategoryId(user, categoryId);

        return ResponseEntity.ok("카테고리가 성공적으로 삭제 되었습니다.");
    }
}
