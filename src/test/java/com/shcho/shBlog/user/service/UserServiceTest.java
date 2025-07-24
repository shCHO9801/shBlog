package com.shcho.shBlog.user.service;

import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.user.dto.UserSignUpRequestDto;
import com.shcho.shBlog.user.entity.User;
import com.shcho.shBlog.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.shcho.shBlog.libs.exception.ErrorCode.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("유저 서비스 테스트")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    public UserServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUpUserSuccess() {
        // given
        UserSignUpRequestDto requestDto = createTestRequest();

        when(userRepository.existsByUsername(requestDto.username())).thenReturn(false);
        when(userRepository.existsByNickname(requestDto.nickname())).thenReturn(false);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");

        // when
        userService.signUp(requestDto);

        // then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertEquals(requestDto.username(), savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(requestDto.nickname(), savedUser.getNickname());
        assertEquals(requestDto.email(), savedUser.getEmail());

        verify(passwordEncoder, times(1)).encode(requestDto.password());
    }

    @Test
    @DisplayName("회원가입 실패 - Username 중복")
    void signUpUserFailedDuplicatedUsername() {
        // given
        UserSignUpRequestDto requestDto = createTestRequest();

        when(userRepository.existsByUsername(requestDto.username())).thenReturn(true);
        when(userRepository.existsByNickname(requestDto.nickname())).thenReturn(false);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.signUp(requestDto));
        assertEquals(DUPLICATED_USERNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 실패 - Nickname 중복")
    void signUpUserFailedDuplicatedNickname() {
        // given
        UserSignUpRequestDto requestDto = createTestRequest();

        when(userRepository.existsByUsername(requestDto.username())).thenReturn(false);
        when(userRepository.existsByNickname(requestDto.nickname())).thenReturn(true);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.signUp(requestDto));
        assertEquals(DUPLICATED_NICKNAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 실패 - Email 중복")
    void signUpUserFailedDuplicatedEmail() {
        // given
        UserSignUpRequestDto requestDto = createTestRequest();

        when(userRepository.existsByUsername(requestDto.username())).thenReturn(false);
        when(userRepository.existsByNickname(requestDto.nickname())).thenReturn(false);
        when(userRepository.existsByEmail(requestDto.email())).thenReturn(true);
        when(passwordEncoder.encode(requestDto.password())).thenReturn("encodedPassword");

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.signUp(requestDto));
        assertEquals(DUPLICATED_EMAIL, exception.getErrorCode());
    }

    private UserSignUpRequestDto createTestRequest() {
        return new UserSignUpRequestDto("newUser", "password", "newNickname", "new@email.com");
    }

}