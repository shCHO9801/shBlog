package com.shcho.shBlog.category.dto;

import com.shcho.shBlog.category.entity.Category;

public record CreateCategoryResponseDto(
        String userName,
        Long categoryId,
        String categoryName,
        String categoryDescription
) {
    public static CreateCategoryResponseDto from(String userName, Category category) {
        return new CreateCategoryResponseDto(
                userName,
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
