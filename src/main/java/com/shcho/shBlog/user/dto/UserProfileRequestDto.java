package com.shcho.shBlog.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserProfileRequestDto(
        @NotBlank String profileImageUrl
) {
}
