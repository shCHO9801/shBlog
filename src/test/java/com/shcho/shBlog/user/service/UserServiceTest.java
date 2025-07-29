package com.shcho.shBlog.user.service;

import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.libs.exception.ErrorCode;
import com.shcho.shBlog.user.dto.UserSignInRequestDto;
import com.shcho.shBlog.user.dto.UserSignUpRequestDto;
import com.shcho.shBlog.user.entity.Role;
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
import static com.shcho.shBlog.user.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("User Service Unit Test")
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

    @Test
    @DisplayName("로그인 성공")
    void signInUserSuccess() {
        // given
        String rawPassword = "wrongPassword";

        User user = User.builder()
                .username("existsUsername")
                .nickname("test")
                .email("test@email.com")
                .password("encodedPassword")
                .role(USER)
                .build();

        UserSignInRequestDto requestDto =
                createSignInRequest(user.getUsername(), rawPassword);

        when(userRepository.existsByUsername(requestDto.username()))
                .thenReturn(true);
        when(userRepository.getReferenceByUsername(requestDto.username()))
                .thenReturn(user);
        when(passwordEncoder.matches(rawPassword, user.getPassword()))
                .thenReturn(true);

        // when
        User signinUser = userService.signIn(requestDto);

        // then
        assertEquals(signinUser.getUsername(), user.getUsername());
        assertEquals(signinUser.getNickname(), user.getNickname());
        assertEquals(signinUser.getEmail(), user.getEmail());
        assertEquals(signinUser.getPassword(), user.getPassword());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 유저")
    void signInUserFailedInvalidUsername() {
        // given
        UserSignInRequestDto signInRequest =
                createSignInRequest("wrongUsername", "password");

        when(userRepository.existsByUsername("wrongUsername"))
                .thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.signIn(signInRequest));

        assertEquals(INVALID_USERNAME_OR_PASSWORD, exception.getErrorCode());
    }

    @Test
    @DisplayName("로그인 실패 - 패스워드 불일치")
    void signInUserFailedInvalidPassword() {
        // given
        String rawPassword = "wrongPassword";

        User user = User.builder()
                .username("existsUsername")
                .nickname("test")
                .email("test@email.com")
                .password("encodedPassword")
                .role(USER)
                .build();

        UserSignInRequestDto signInRequest =
                createSignInRequest(user.getUsername(), rawPassword);

        when(userRepository.existsByUsername(signInRequest.username()))
                .thenReturn(true);
        when(userRepository.getReferenceByUsername(signInRequest.username()))
                .thenReturn(user);
        when(passwordEncoder.matches(rawPassword, user.getPassword()))
                .thenReturn(false);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> userService.signIn(signInRequest));

        assertEquals(INVALID_USERNAME_OR_PASSWORD, exception.getErrorCode());
    }

    private UserSignInRequestDto createSignInRequest(String username, String password) {
        return new UserSignInRequestDto(username, password);
    }
}