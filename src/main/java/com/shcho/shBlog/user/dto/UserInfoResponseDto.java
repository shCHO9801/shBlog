package com.shcho.shBlog.user.dto;

import com.shcho.shBlog.user.entity.User;

public record UserInfoResponseDto(
        String username,
        String nickname,
        String email,
        String profileImageUrl
) {
    public static UserInfoResponseDto from(User user) {
        return new UserInfoResponseDto(
                user.getUsername(),
                user.getNickname(),
                user.getEmail(),
                user.getProfileImageUrl()
        );
    }
}
