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

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * 연관관계 편의 메서드(User.addCategory)를 통해서만 사용해야 한다.
     */
    public void setUser(User user) {
        this.user = user;
    }
}
