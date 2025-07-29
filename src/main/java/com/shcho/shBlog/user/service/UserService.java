package com.shcho.shBlog.user.service;

import com.shcho.shBlog.common.util.JwtProvider;
import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.user.dto.UserSignInRequestDto;
import com.shcho.shBlog.user.dto.UserSignUpRequestDto;
import com.shcho.shBlog.user.entity.User;
import com.shcho.shBlog.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.shcho.shBlog.libs.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public User signUp(@Valid UserSignUpRequestDto requestDto) {

        String username = requestDto.username();
        String password = requestDto.password();
        String nickname = requestDto.nickname();
        String email = requestDto.email();

        if (userRepository.existsByUsername(username)) {
            throw new CustomException(DUPLICATED_USERNAME);
        }

        if (userRepository.existsByEmail(email)) {
            throw new CustomException(DUPLICATED_EMAIL);
        }

        if (userRepository.existsByNickname(nickname)) {
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

    public User signIn(
            @Valid UserSignInRequestDto requestDto
    ) {
        String username = requestDto.username();
        String password = requestDto.password();

        if (!userRepository.existsByUsername(username) || isUnmatchedPassword(username, password)) {
            throw new CustomException(INVALID_USERNAME_OR_PASSWORD);
        }

        return userRepository.getReferenceByUsername(username);
    }

    private boolean isUnmatchedPassword(String username, String password) {
        User user = userRepository.getReferenceByUsername(username);
        return !passwordEncoder.matches(password, user.getPassword());
    }

    public String getUserToken(User user) {
        return jwtProvider.createToken(user.getUsername(), user.getRole());
    }
}
