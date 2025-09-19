package com.shcho.shBlog.myPage.service;

import com.shcho.shBlog.common.service.S3Service;
import com.shcho.shBlog.user.entity.User;
import com.shcho.shBlog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final UserRepository userRepository;
    private final S3Service s3Service;

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
}
