package com.shcho.shBlog.category.entity;

import com.shcho.shBlog.common.entity.BaseEntity;
import com.shcho.shBlog.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static Category of(User user, String name, String description) {
        return Category.builder()
                .user(user)
                .name(name)
                .description(description)
                .build();
    }
}
