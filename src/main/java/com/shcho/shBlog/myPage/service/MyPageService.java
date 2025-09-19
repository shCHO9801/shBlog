package com.shcho.shBlog.myPage.service;

import com.shcho.shBlog.common.service.S3Service;
import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.libs.exception.ErrorCode;
import com.shcho.shBlog.user.entity.User;
import com.shcho.shBlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shcho.shBlog.libs.exception.ErrorCode.DUPLICATED_NICKNAME;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void updateProfileImage(Long userId, String newImageUrl) {
        User user = userRepository.getReferenceById(userId);

        deleteOldImageUrl(user);

        user.updateProfileImageUrl(newImageUrl);
    }

    @Transactional
    public void deleteProfileImage(Long userId) {
        User user = userRepository.getReferenceById(userId);

        deleteOldImageUrl(user);

        user.deleteProfileImageUrl();
    }

    private void deleteOldImageUrl(User user) {
        String oldImageUrl = user.getProfileImageUrl();

        if(oldImageUrl != null && !oldImageUrl.isBlank()) {
            s3Service.deleteFileByUrl(oldImageUrl);
        }
    }

    public boolean existsByNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    @Transactional
    public void updateNickname(Long userId, String newNickname) {
        User user = userRepository.getReferenceById(userId);

        if(userRepository.existsByNickname(newNickname)) {
            throw new CustomException(DUPLICATED_NICKNAME);
        }

        user.updateNickname(newNickname);
    }

    @Transactional
    public void updatePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.getReferenceById(userId);

        if(!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_USERNAME_OR_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.updatePassword(encodedPassword);
    }
}
