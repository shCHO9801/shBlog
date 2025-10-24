package com.shcho.shBlog.category.dto;

import com.shcho.shBlog.category.entity.Category;

public record CategoryResponseDto(
        Long id,
        String name,
        String description
) {
    public static CategoryResponseDto from(Category category) {
        return new CategoryResponseDto(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }
}
