package com.shcho.shBlog.myPage.dto;

import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateRequestDto(
        @NotBlank String currentPassword,
        @NotBlank String newPassword
) {
}
