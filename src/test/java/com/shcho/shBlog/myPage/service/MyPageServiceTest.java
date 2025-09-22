package com.shcho.shBlog.myPage.service;

import com.shcho.shBlog.common.service.S3Service;
import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.user.entity.User;
import com.shcho.shBlog.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.shcho.shBlog.libs.exception.ErrorCode.DUPLICATED_NICKNAME;
import static com.shcho.shBlog.libs.exception.ErrorCode.INVALID_USERNAME_OR_PASSWORD;
import static com.shcho.shBlog.user.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayName("MyPage Service Unit Test")
class MyPageServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MyPageService myPageService;

    public MyPageServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("프로필 사진 업데이트 성공")
    void updateUserProfileImageSuccess() {
        // given
        Long userId = 1L;
        String newImageUrl = "www.minio.com/new.jpg";
        String oldImageUrl = "www.minio.com/old.jpg";

        User user = User.builder()
                .userId(userId)
                .username("existsUsername")
                .nickname("test")
                .email("test@email.com")
                .password("encodedPassword")
                .profileImageUrl(oldImageUrl)
                .role(USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // when
        myPageService.updateProfileImage(userId, newImageUrl);

        // then
        verify(s3Service, times(1)).deleteFileByUrl(oldImageUrl);
        assertEquals(newImageUrl, user.getProfileImageUrl());

    }

    @Test
    @DisplayName("프로필 사진 업데이트 성공 - 기존 이미지 없음")
    void updateUserProfileImageWithoutOldImage() {
        // given
        Long userId = 1L;
        String newImageUrl = "www.minio.com/new.jpg";

        User user = User.builder()
                .userId(userId)
                .username("existsUsername")
                .nickname("test")
                .email("test@email.com")
                .password("encodedPassword")
                .profileImageUrl(null)
                .role(USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // when
        myPageService.updateProfileImage(userId, newImageUrl);
        verify(s3Service, never()).deleteFileByUrl(newImageUrl);
    }

    @Test
    @DisplayName("프로필 사진 삭제 성공")
    void deleteUserProfileImageSuccess() {
        // given
        Long userId = 1L;
        String oldImageUrl = "www.minio.com/old.jpg";

        User user = User.builder()
                .userId(userId)
                .username("existsUsername")
                .nickname("test")
                .email("test@email.com")
                .password("encodedPassword")
                .profileImageUrl(oldImageUrl)
                .role(USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // when
        myPageService.deleteProfileImage(userId);

        // then
        verify(s3Service, times(1)).deleteFileByUrl(oldImageUrl);
        assertNull(user.getProfileImageUrl());
    }

    @Test
    @DisplayName("프로필 사진 삭제 - 기존 이미지 없음")
    void deleteUserProfileImageWithoutOldImage() {
        // given
        Long userId = 1L;

        User user = User.builder()
                .userId(userId)
                .username("existsUsername")
                .nickname("test")
                .email("test@email.com")
                .password("encodedPassword")
                .profileImageUrl(null) // 이미지 없음
                .role(USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);

        // when
        myPageService.deleteProfileImage(userId);

        // then
        verify(s3Service, never()).deleteFileByUrl(anyString());
        assertNull(user.getProfileImageUrl());
    }

    @Test
    @DisplayName("닉네임 중복 확인")
    void existsNicknameSuccess() {
        // given
        String nickname = "existsNickname";
        when(userRepository.existsByNickname(nickname)).thenReturn(true);

        // when
        boolean exists = myPageService.existsByNickname(nickname);

        // then
        assertTrue(exists);
        verify(userRepository, times(1)).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임 변경 성공")
    void updateNicknameSuccess() {
        // given
        Long userId = 1L;
        String newNickname = "newNickname";

        User user = User.builder()
                .userId(userId)
                .username("userName")
                .nickname("oldNickname")
                .password("encodedPassword")
                .role(USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(userRepository.existsByNickname(newNickname)).thenReturn(false);

        // when
        myPageService.updateNickname(userId, newNickname);

        // then
        assertEquals(newNickname, user.getNickname());
        verify(userRepository, times(1)).existsByNickname(newNickname);
    }

    @Test
    @DisplayName("닉네임 변경 실패 - 중복된 닉네임")
    void updateNicknameFailedDuplicatedNickname() {
        // given
        Long userId = 1L;
        String existsNickname = "existsNickname";

        User user = User.builder()
                .userId(userId)
                .username("userName")
                .nickname("oldNickname")
                .password("encodedPassword")
                .role(USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(userRepository.existsByNickname(existsNickname)).thenReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> myPageService.updateNickname(userId, existsNickname));

        assertEquals(DUPLICATED_NICKNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    void updatePasswordSuccess() {
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        String encodedOldPassword = "encodedOldPassword";
        String encodedNewPassword = "encodedNewPassword";

        User user = User.builder()
                .userId(userId)
                .username("existsUsername")
                .nickname("test")
                .password(encodedOldPassword)
                .role(USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // when
        myPageService.updatePassword(userId, oldPassword, newPassword);

        // then
        assertEquals(encodedNewPassword, user.getPassword());
        verify(passwordEncoder).matches(oldPassword, encodedOldPassword);
        verify(passwordEncoder).encode(newPassword);
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 비밀번호 불일치")
    void updatePasswordFailedUnmatchedPassword() {
        // given
        Long userId = 1L;
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";

        String encodedOldPassword = passwordEncoder.encode(oldPassword);

        User user = User.builder()
                .userId(userId)
                .username("existsUsername")
                .nickname("test")
                .password(encodedOldPassword)
                .role(USER)
                .build();

        when(userRepository.getReferenceById(userId)).thenReturn(user);
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(false);


        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> myPageService.updatePassword(userId, oldPassword, newPassword));

        assertEquals(INVALID_USERNAME_OR_PASSWORD, exception.getErrorCode());
    }
}