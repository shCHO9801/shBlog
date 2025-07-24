package com.shcho.shBlog.user.entity;

import com.shcho.shBlog.common.entity.BaseEntity;
import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.libs.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String nickname;

    @Column(unique = true)
    private String email;

    @Column
    String profileImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(50)")
    private Role role;

    @Column
    private LocalDateTime deletedAt;

    public void updateUsername(String username) {
        this.username = username;
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void withdraw() {
        if (this.deletedAt != null) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_USER);
        }
        this.deletedAt = LocalDateTime.now();
        this.username = "DELETED_" + this.username;
        this.password = null;
        this.nickname = null;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}
