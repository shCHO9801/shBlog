package com.shcho.shBlog.category.service;

import com.shcho.shBlog.category.dto.CreateCategoryRequestDto;
import com.shcho.shBlog.category.entity.Category;
import com.shcho.shBlog.category.repository.CategoryRepository;
import com.shcho.shBlog.libs.exception.CustomException;
import com.shcho.shBlog.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static com.shcho.shBlog.libs.exception.ErrorCode.*;
import static com.shcho.shBlog.user.entity.Role.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Category Service Unit Test")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    public CategoryServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("카테고리 생성 성공")
    void createCategorySuccess() {
        // given
        Long userId = 1L;

        User user = User.builder()
                .userId(userId)
                .username("userName")
                .nickname("nickname")
                .password("encodedPassword")
                .role(USER)
                .build();

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("name", "description");

        when(categoryRepository.existsByUser_UserIdAndName(user.getUserId(), requestDto.name()))
                .thenReturn(false);

        // when
        Category newCategory = categoryService.createCategory(user, requestDto);

        // then
        assertNotNull(newCategory);
        assertEquals(user.getUserId(), newCategory.getUser().getUserId());
        assertEquals(requestDto.name(), newCategory.getName());
        assertEquals(requestDto.description(), newCategory.getDescription());
        verify(categoryRepository, times(1)).existsByUser_UserIdAndName(user.getUserId(), requestDto.name());
    }

    @Test
    @DisplayName("카테고리 생성 실패 - 이미 존재하는 카테고리 이름")
    void createCategoryFailedDuplicateCategoryName() {
        // given
        User user = User.builder()
                .userId(1L)
                .role(USER)
                .build();

        CreateCategoryRequestDto requestDto = new CreateCategoryRequestDto("existsCategory", "description");
        when(categoryRepository.existsByUser_UserIdAndName(user.getUserId(), requestDto.name()))
                .thenReturn(true);

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.createCategory(user, requestDto));

        assertEquals(DUPLICATE_CATEGORY_NAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("유저의 모든 카테고리 조회 성공")
    void getAllCategoriesSuccess() {
        // given
        Long userId = 1L;
        User user = User.builder()
                .userId(userId)
                .role(USER)
                .build();

        List<Category> mockCategories = List.of(
                Category.of(user, "카테고리1", "설명1"),
                Category.of(user, "카테고리2", "설명2")
        );

        when(categoryRepository.findAllByUser_UserIdOrderByNameAsc(userId))
                .thenReturn(mockCategories);

        // when
        List<Category> result = categoryService.getAllCategories(user);

        // then
        assertEquals(2, result.size());
        assertEquals("카테고리1", result.get(0).getName());
        verify(categoryRepository, times(1)).findAllByUser_UserIdOrderByNameAsc(userId);
    }

    @Test
    @DisplayName("카테고리 삭제 성공")
    void deleteCategorySuccess() {
        // given
        Long userId = 1L;
        Long categoryId = 10L;

        User user = User.builder()
                .userId(userId)
                .role(USER)
                .build();

        Category category = Category.of(user, "testCategory", "description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        // when
        categoryService.deleteCategoryByCategoryId(user, categoryId);

        // then
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 존재하지 않는 카테고리")
    void deleteCategoryFailedCategoryNotFound() {
        // given
        Long categoryId = 999L;
        User user = User.builder()
                .userId(1L)
                .role(USER)
                .build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.deleteCategoryByCategoryId(user, categoryId));

        assertEquals(CATEGORY_NOT_FOUND, exception.getErrorCode());
        verify(categoryRepository, never()).delete(any());
    }

    @Test
    @DisplayName("카테고리 삭제 실패 - 카테고리의 소유 유저 불일치")
    void deleteCategoryFailedForbiddenCategory() {
        // given
        User categoryOwner = User.builder()
                .userId(1L)
                .role(USER)
                .build();

        User anotherUser = User.builder()
                .userId(2L)
                .role(USER)
                .build();

        Category category = Category.of(categoryOwner, "카테고리", "설명");

        when(categoryRepository.findById(anyLong()))
                .thenReturn(java.util.Optional.of(category));

        // when & then
        CustomException exception = assertThrows(CustomException.class,
                () -> categoryService.deleteCategoryByCategoryId(anotherUser, 1L));

        assertEquals(FORBIDDEN_CATEGORY_DELETE, exception.getErrorCode());
        verify(categoryRepository, never()).delete(any());
    }
}