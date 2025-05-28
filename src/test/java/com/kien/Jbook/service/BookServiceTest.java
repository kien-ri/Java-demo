package com.kien.Jbook.service;

import com.kien.Jbook.common.CustomException;
import com.kien.Jbook.mapper.BookMapper;
import com.kien.Jbook.model.Book;
import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookCreate;
import com.kien.Jbook.model.dto.book.BookView;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.getById(bookId);
            });
            String expectedMsg = "入力された値が無効です。";
            assertThat(e.getMessage()).isEqualTo(expectedMsg);

            verify(bookMapper, never()).getById(any());
        }

        @Test
        void throwCustomExceptionWhenIdIsZero() {
            Long bookId = 0L;
            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.getById(bookId);
            });
            String expectedMsg = "入力された値が無効です。";
            assertThat(e.getMessage()).isEqualTo(expectedMsg);

            verify(bookMapper, never()).getById(any());
        }

        @Test
        void throwCustomExceptionWhenIdIsNull() {
            Long bookId = null;
            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.getById(bookId);
            });
            String expectedMsg = "入力された値が無効です。";
            assertThat(e.getMessage()).isEqualTo(expectedMsg);

            verify(bookMapper, never()).getById(any());
        }
    }

    @Nested
    class RegisterTest{
        @Test
        void returnBookCreatedResponseWhenRegisterSucceedsWithoutId() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    2500
            );

            ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
            when(bookMapper.save(captor.capture())).thenAnswer(invocation -> {
                captor.getValue().setId(1L);
                return 1;
            });

            BookBasicInfo expectedResult = new BookBasicInfo(1L, "Kotlin入門");
            BookBasicInfo result = bookService.register(bookCreate);

            assertEquals(expectedResult, result);
            verify(bookMapper, times(1)).save(any());
        }

        @Test
        void returnBookCreatedResponseWhenRegisterSucceedsWithValidId() {
            BookCreate bookCreate = new BookCreate(
                    222L,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    2500
            );

            ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);
            when(bookMapper.save(captor.capture())).thenAnswer(invocation -> {
                captor.getValue().setId(222L);
                return 1;
            });

            BookBasicInfo expectedResult = new BookBasicInfo(222L, "Kotlin入門");
            BookBasicInfo result = bookService.register(bookCreate);

            assertEquals(expectedResult, result);
            verify(bookMapper, times(1)).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenIdIsNegative() {
            BookCreate bookCreate = new BookCreate(
                    -1L,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    2500
            );

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("入力された値が無効です。", e.getMessage());
            assertEquals("id", e.getField());
            assertEquals(-1L, e.getValue());
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());

            verify(bookMapper, never()).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenIdIsZero() {
            BookCreate bookCreate = new BookCreate(
                    0L,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    2500
            );

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("入力された値が無効です。", e.getMessage());
            assertEquals("id", e.getField());
            assertEquals(0L, e.getValue());
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
            verify(bookMapper, never()).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenPriceIsNegative() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    -500
            );

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("入力された値が無効です。", e.getMessage());
            assertEquals("price", e.getField());
            assertEquals(-500, e.getValue());
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
            verify(bookMapper, never()).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenPublisherIdIsNegative() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    -1L,
                    1L,
                    2500
            );

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("入力された値が無効です。", e.getMessage());
            assertEquals("publisherId", e.getField());
            assertEquals(-1L, e.getValue());
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
            verify(bookMapper, never()).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenPublisherIdIsZero() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    0L,
                    1L,
                    2500
            );

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("入力された値が無効です。", e.getMessage());
            assertEquals("publisherId", e.getField());
            assertEquals(0L, e.getValue());
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
            verify(bookMapper, never()).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenUserIdIsNegative() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    -1L,
                    2500
            );

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("入力された値が無効です。", e.getMessage());
            assertEquals("userId", e.getField());
            assertEquals(-1L, e.getValue());
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
            verify(bookMapper, never()).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenUserIdIsZero() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    0L,
                    2500
            );

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("入力された値が無効です。", e.getMessage());
            assertEquals("userId", e.getField());
            assertEquals(0L, e.getValue());
            assertEquals(HttpStatus.BAD_REQUEST, e.getHttpStatus());
            verify(bookMapper, never()).save(any());
        }

        @Test
        void shouldThrowDuplicateKeyExceptionWhenIdIsDuplicated() {
            BookCreate bookCreate = new BookCreate(
                    1L,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    2500
            );

            CustomException expectedError = new CustomException(
                    "プライマリキーが重複しました。別の値にしてください",
                    HttpStatus.CONFLICT,
                    "id",
                    1L
            );

            DuplicateKeyException springError = new DuplicateKeyException("模擬主キー重複エラー");
            when(bookMapper.save(any())).thenThrow(springError);

            CustomException realError = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals(expectedError.getMessage(), realError.getMessage());
            assertEquals(expectedError.getField(), realError.getField());
            assertEquals(expectedError.getValue(), realError.getValue());

            verify(bookMapper, times(1)).save(any());
        }

        @Test
        void shouldThrowDataIntegrityViolationExceptionWhenPublisherIdDoesNotExist() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    999L,
                    1L,
                    2500
            );
            CustomException expectedError = new CustomException(
                    "存在しない外部キーです。",
                    HttpStatus.BAD_REQUEST,
                    "publisherId",
                    999L
            );

            SQLIntegrityConstraintViolationException sqlException = new SQLIntegrityConstraintViolationException(
                    "FOREIGN KEY (`publisher_id`) violation",
                    "FOREIGN_KEY",
                    1452,
                    null
            );
            DataIntegrityViolationException springException = new DataIntegrityViolationException(
                    "FOREIGN KEY (`publisher_id`) violation",
                    sqlException
            );
            when(bookMapper.save(any())).thenThrow(springException);

            CustomException realError = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals(expectedError.getMessage(), realError.getMessage());
            assertEquals(expectedError.getField(), realError.getField());
            assertEquals(expectedError.getValue(), realError.getValue());

            verify(bookMapper, times(1)).save(any());
        }

        @Test
        void shouldThrowDataIntegrityViolationExceptionWhenUserIdDoesNotExist() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    999L,
                    2500
            );
            CustomException expectedError = new CustomException(
                    "存在しない外部キーです。",
                    HttpStatus.BAD_REQUEST,
                    "userId",
                    999L
            );

            SQLIntegrityConstraintViolationException sqlException = new SQLIntegrityConstraintViolationException(
                    "FOREIGN KEY (`user_id`) violation",
                    "FOREIGN_KEY",
                    1452,
                    null
            );
            DataIntegrityViolationException springException = new DataIntegrityViolationException(
                    "FOREIGN KEY (`user_id`) violation",
                    sqlException
            );
            when(bookMapper.save(any())).thenThrow(springException);

            CustomException realError = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals(expectedError.getMessage(), realError.getMessage());
            assertEquals(expectedError.getField(), realError.getField());
            assertEquals(expectedError.getValue(), realError.getValue());

            verify(bookMapper, times(1)).save(any());
        }

        @Test
        void shouldThrowExceptionWhenVendorCodeIsNot1452() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    999L,
                    1L,
                    2500
            );
            SQLIntegrityConstraintViolationException sqlException = new SQLIntegrityConstraintViolationException(
                    "模擬予想外SQLIntegrityConstraintViolationException",
                    "予想外",
                    1111,
                    null
            );
            DataIntegrityViolationException expectedException = new DataIntegrityViolationException(
                    "模擬予想外DataIntegrityViolationException",
                    sqlException
            );
            when(bookMapper.save(any())).thenThrow(expectedException);

            assertThrows(DataIntegrityViolationException.class, () -> {
                bookService.register(bookCreate);
            });

            verify(bookMapper, times(1)).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenInsertFails() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    2500
            );
            when(bookMapper.save(any())).thenReturn(0);

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("書籍情報が正しく登録されませんでした。", e.getMessage());

            verify(bookMapper, times(1)).save(any());
        }

        @Test
        void shouldThrowCustomExceptionWhenIdIsNotGenerated() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    2500
            );
            when(bookMapper.save(any())).thenReturn(1);

            CustomException e = assertThrows(CustomException.class, () -> {
                bookService.register(bookCreate);
            });
            assertEquals("書籍情報保存に失敗しました：IDが生成されませんでした", e.getMessage());

            verify(bookMapper, times(1)).save(any());
        }

        @Test
        void shouldThrowRuntimeExceptionWhenUnexpectedErrorOccurs() {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    1L,
                    2500
            );
            when(bookMapper.save(any())).thenThrow(new RuntimeException("模擬予想外エラー"));

            assertThrows(RuntimeException.class, () -> {
                bookService.register(bookCreate);
            });

            verify(bookMapper, times(1)).save(any());
        }
    }
}
