package com.shcho.shBlog.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserSignInRequestDto(
        @NotBlank String username,
        @NotBlank String password
) {
}
