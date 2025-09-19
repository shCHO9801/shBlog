package com.shcho.shBlog.myPage.service;

import com.shcho.shBlog.common.service.S3Service;
import com.shcho.shBlog.user.entity.User;
import com.shcho.shBlog.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.shcho.shBlog.user.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@DisplayName("MyPage Service Unit Test")
class MyPageServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private S3Service s3Service;

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
}