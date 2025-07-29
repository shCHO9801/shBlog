package com.shcho.shBlog.user.dto;

import com.shcho.shBlog.user.entity.User;

public record UserSignInResponseDto(
        String token,
        String username,
        String nickname,
        String email
) {
    public static UserSignInResponseDto of(User user, String token) {
        return new UserSignInResponseDto(
                token,
                user.getUsername(),
                user.getNickname(),
                user.getEmail()
        );
    }
}
