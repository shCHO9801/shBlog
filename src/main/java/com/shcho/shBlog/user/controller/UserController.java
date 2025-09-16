package com.shcho.shBlog.user.controller;

import com.shcho.shBlog.auth.CustomUserDetails;
import com.shcho.shBlog.user.dto.*;
import com.shcho.shBlog.user.entity.User;
import com.shcho.shBlog.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpResponseDto> signUp(
            @Valid @RequestBody UserSignUpRequestDto requestDto
    ) {

        User signUpUser = userService.signUp(requestDto);
        UserSignUpResponseDto responseDto = UserSignUpResponseDto.from(signUpUser);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/signin")
    public ResponseEntity<UserSignInResponseDto> signIn(
            @Valid @RequestBody UserSignInRequestDto requestDto
    ) {
        User user = userService.signIn(requestDto);
        String token = userService.getUserToken(user);

        UserSignInResponseDto responseDto = UserSignInResponseDto.of(user, token);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        User user = userDetails.getUser();
        UserInfoResponseDto responseDto = UserInfoResponseDto.from(user);

        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/profile")
    public ResponseEntity<String> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserProfileRequestDto requestDto
    ) {
        Long userId = userDetails.getUser().getUserId();
        userService.updateProfileImage(userId, requestDto.profileImageUrl());
        return ResponseEntity.ok("프로필 이미지 등록 완료");
    }

    @DeleteMapping("/profile")
    public ResponseEntity<String> deleteProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUser().getUserId();
        userService.deleteProfileImage(userId);
        return ResponseEntity.ok("프로필 이미지 삭제 완료");
    }
}

