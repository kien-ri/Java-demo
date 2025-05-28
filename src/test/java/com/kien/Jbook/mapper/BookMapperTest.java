package com.kien.Jbook.mapper;

import com.kien.Jbook.model.Book;
import com.kien.Jbook.model.dto.book.BookView;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    @Nested
    @Sql(scripts = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
    @Sql(
            scripts = {
                    "/mapper/data/books/getById/publisher.sql",
                    "/mapper/data/books/getById/user.sql",
                    "/mapper/data/books/getById/books.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
    )
    class GetByIdTest {

        @Test
        void returnBookViewWhenBookExists() {
            Long bookId = 1L;
            BookView result = bookMapper.getById(bookId);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(
                    new BookView(
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
                            LocalDateTime.of(2023, 1, 1, 10, 0),
                            LocalDateTime.of(2023, 1, 1, 10, 0)
                    )
            );
        }

        @Test
        void returnNullWhenBookDoesNotExist() {
            BookView result = bookMapper.getById(999L);
            assertThat(result).isNull();
        }

        @Test
        void returnNullWhenBookIsLogicallyDeleted() {
            BookView result = bookMapper.getById(2L);
            assertThat(result).isNull();
        }

        @Test
        void returnBookViewWithNullPublisherInfoWhenPublisherIsDeleted() {
            Long bookId = 3L;
            BookView result = bookMapper.getById(bookId);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(
                    new BookView(
                            bookId,
                            "Java入門",
                            "ジャバー ニュウモン",
                            "田中太郎",
                            null,
                            null,
                            100L,
                            "テストユーザー",
                            2000,
                            false,
                            LocalDateTime.of(2023, 1, 1, 10, 0),
                            LocalDateTime.of(2023, 1, 1, 10, 0)
                    )
            );
        }

        @Test
        void returnBookViewWithNullUserInfoWhenUserIsDeleted() {
            Long bookId = 4L;
            BookView result = bookMapper.getById(bookId);

            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(
                    new BookView(
                            bookId,
                            "Spring Boot 入門",
                            "スプリング ブート ニュウモン",
                            "佐藤次郎",
                            1L,
                            "技術出版社",
                            null,
                            null,
                            3000,
                            false,
                            LocalDateTime.of(2023, 2, 1, 10, 0),
                            LocalDateTime.of(2023, 2, 1, 10, 0)
                    )
            );
        }
    }

    @Nested
    @Sql(scripts = {"/schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
    @Sql(
            scripts = {
                    "/mapper/data/books/update/publisher.sql",
                    "/mapper/data/books/update/user.sql",
                    "/mapper/data/books/update/books.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
    )
    class UpdateTest{
        @Test
        void updateShouldUpdateBookAndReturnAffectedRows() {
            LocalDateTime current = LocalDateTime.of(2025, 5, 15, 16, 10, 30);
            Book book = new Book(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    100L,
                    4200,
                    false,
                    null,
                    current
            );

            BookView expectedResult = new BookView(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    "技術出版社",
                    100L,
                    "テストユーザー",
                    4200,
                    false,
                    LocalDateTime.of(2023, 1, 1, 10, 0, 0),
                    current
            );

            BookView pre = bookMapper.getById(1L);
            assertThat(pre.getTitle()).isEqualTo("Kotlin入門");

            int affectedRows = bookMapper.update(book);
            assertThat(affectedRows).isEqualTo(1);

            BookView updatedBook = bookMapper.getById(1L);
            assertThat(updatedBook).isEqualTo(expectedResult);
        }

        @Test
        void returnZeroWhenBookDoesNotExist() {
            LocalDateTime current = LocalDateTime.of(2025, 5, 15, 16, 10, 30);
            Book book = new Book(
                    999L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    100L,
                    4200,
                    false,
                    null,
                    current
            );

            BookView nothing = bookMapper.getById(999L);
            assertThat(nothing).isNull();

            int affectedRows = bookMapper.update(book);
            assertThat(affectedRows).isEqualTo(0);
        }

        @Test
        void returnZeroWhenBookIsLogicallyDeleted() {
            LocalDateTime current = LocalDateTime.of(2025, 5, 15, 16, 10, 30);
            Book book = new Book(
                    2L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    100L,
                    4200,
                    false,
                    null,
                    current
            );

            BookView nothing = bookMapper.getById(2L);
            assertThat(nothing).isNull();

            int affectedRows = bookMapper.update(book);
            assertThat(affectedRows).isEqualTo(0);
        }

        @Test
        void throwExceptionWhenPublisherIdDoesNotExist() {
            LocalDateTime current = LocalDateTime.of(2025, 5, 15, 16, 10, 30);
            Book book = new Book(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    999L,
                    100L,
                    4200,
                    false,
                    null,
                    current
            );

            Throwable thrown = catchThrowable(() -> bookMapper.update(book));
            assertThat(thrown).isInstanceOf(DataIntegrityViolationException.class);
            Throwable rootCause = thrown.getCause();
            assertThat(rootCause).isInstanceOf(SQLIntegrityConstraintViolationException.class);
            int errorCode = ((SQLIntegrityConstraintViolationException) rootCause).getErrorCode();
            assertThat(errorCode).isEqualTo(1452);
        }

        @Test
        void throwExceptionWhenUserIdDoesNotExist() {
            LocalDateTime current = LocalDateTime.of(2025, 5, 15, 16, 10, 30);
            Book book = new Book(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    999L,
                    4200,
                    false,
                    null,
                    current
            );

            Throwable thrown = catchThrowable(() -> bookMapper.update(book));
            assertThat(thrown).isInstanceOf(DataIntegrityViolationException.class);
            Throwable rootCause = thrown.getCause();
            assertThat(rootCause).isInstanceOf(SQLIntegrityConstraintViolationException.class);
            int errorCode = ((SQLIntegrityConstraintViolationException) rootCause).getErrorCode();
            assertThat(errorCode).isEqualTo(1452);
        }
    }
}
