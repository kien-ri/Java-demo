package com.kien.Jbook.service;

import com.kien.Jbook.common.exception.InvalidParamCustomException;
import com.kien.Jbook.mapper.BookMapper;
import com.kien.Jbook.model.dto.book.BookView;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BookServiceTest {

    @MockitoBean
    private BookMapper bookMapper;

    @Autowired
    private BookService bookService;

    @Nested
    class GetBookByIdTest {

        @Test
        void returnBookViewWhenBookExists() {
            Long bookId = 1L;
            BookView bookView = new BookView(
                    bookId,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    "技術出版社",
                    100L,
                    "テストユーザー",
                    2500,
                    false,
                    LocalDateTime.of(2025, 4, 28, 10, 0),
                    LocalDateTime.of(2025, 4, 28, 10, 0)
            );
            when(bookMapper.getById(bookId)).thenReturn(bookView);

            BookView result = bookService.getById(bookId);
            assertThat(result).isEqualTo(bookView);

            verify(bookMapper, times(1)).getById(any());
        }

        @Test
        void returnNullWhenBookDoesNotExist() {
            Long bookId = 1L;
            when(bookMapper.getById(bookId)).thenReturn(null);
            BookView result = bookService.getById(bookId);
            assertThat(result).isNull();

            verify(bookMapper, times(1)).getById(any());
        }

        @Test
        void throwCustomExceptionWhenIdIsNegative() {
            Long bookId = -1L;
            InvalidParamCustomException e = assertThrows(InvalidParamCustomException.class, () -> {
                bookService.getById(bookId);
            });
            String expectedMsg = "入力された値が無効です。";
            assertThat(e.getMessage()).isEqualTo(expectedMsg);

            verify(bookMapper, never()).getById(any());
        }

        @Test
        void throwCustomExceptionWhenIdIsZero() {
            Long bookId = 0L;
            InvalidParamCustomException e = assertThrows(InvalidParamCustomException.class, () -> {
                bookService.getById(bookId);
            });
            String expectedMsg = "入力された値が無効です。";
            assertThat(e.getMessage()).isEqualTo(expectedMsg);

            verify(bookMapper, never()).getById(any());
        }

        @Test
        void throwCustomExceptionWhenIdIsNull() {
            Long bookId = null;
            InvalidParamCustomException e = assertThrows(InvalidParamCustomException.class, () -> {
                bookService.getById(bookId);
            });
            String expectedMsg = "入力された値が無効です。";
            assertThat(e.getMessage()).isEqualTo(expectedMsg);

            verify(bookMapper, never()).getById(any());
        }
    }
}
