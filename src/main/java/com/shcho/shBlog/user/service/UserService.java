package com.shcho.shBlog.user.service;

import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.libs.exception.ErrorCode;
import com.shcho.shBlog.user.dto.UserSignUpRequestDto;
import com.shcho.shBlog.user.entity.User;
import com.shcho.shBlog.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shcho.shBlog.libs.exception.ErrorCode.*;
import static com.shcho.shBlog.libs.exception.ErrorCode.DUPLICATED_EMAIL;
import static com.shcho.shBlog.libs.exception.ErrorCode.DUPLICATED_USERNAME;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User signUp(@Valid UserSignUpRequestDto requestDto) {

        String username = requestDto.username();
        String password = requestDto.password();
        String nickname = requestDto.nickname();
        String email = requestDto.email();

        if(userRepository.existsByUsername(username)) {
            throw new CustomException(DUPLICATED_USERNAME);
        }

        if(userRepository.existsByEmail(email)) {
            throw new CustomException(DUPLICATED_EMAIL);
        }

        if(userRepository.existsByNickname(nickname)) {
            throw new CustomException(DUPLICATED_NICKNAME);
        }

        String encodedPassword = passwordEncoder.encode(password);

        User signUpUser = User.of(
                username,
                encodedPassword,
                nickname,
                email
        );

        return userRepository.save(signUpUser);
    }
}
