package com.shcho.shBlog.myPage.controller;

import com.shcho.shBlog.auth.CustomUserDetails;
import com.shcho.shBlog.myPage.service.MyPageService;
import com.shcho.shBlog.myPage.dto.UserInfoResponseDto;
import com.shcho.shBlog.user.dto.UserProfileRequestDto;
import com.shcho.shBlog.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/myPage")
@RequiredArgsConstructor
public class MyPageController {

    private MyPageService myPageService;

    @GetMapping
    public ResponseEntity<UserInfoResponseDto> getMyPage(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        UserInfoResponseDto responseDto = UserInfoResponseDto.from(user);

        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserProfileRequestDto requestDto
    ) {
        Long userId = userDetails.getUser().getUserId();
        myPageService.updateProfileImage(userId, requestDto.profileImageUrl());
        return ResponseEntity.ok("프로필 이미지 등록 완료");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        myPageService.deleteProfileImage(userId);
        return ResponseEntity.ok("프로필 이미지 삭제 완료");
    }

    // TODO :
}
