package com.kien.Jbook.mapper;

import com.kien.Jbook.model.Book;
import com.kien.Jbook.model.dto.book.BookView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
                    "/mapper/data/books/save/publisher.sql",
                    "/mapper/data/books/save/user.sql",
                    "/mapper/data/books/save/books.sql"
            },
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
    )
    class SaveTest{

        @Autowired
        BooksTestMapper booksTestMapper;

        @BeforeEach
        void resetAutoIncrement() {
            booksTestMapper.resetAutoIncrement();
        }

        @Test
        void saveShouldInsertBookWithoutId() {
            Long currentMaxId = booksTestMapper.getMaxId();
            assertThat(currentMaxId).isEqualTo(4L);

            LocalDateTime currentTime = LocalDateTime.of(2025, 5, 4, 13, 20, 10);

            Book book = new Book(
                    null,
                    "Python入門",
                    "パイソン ニュウモン",
                    "佐藤花子",
                    1L,
                    100L,
                    2500,
                    false,
                    currentTime,
                    currentTime
            );
            int affectedRows = bookMapper.save(book);
            assertThat(affectedRows).isEqualTo(1);
            assertThat(book.getId()).isEqualTo(5L);

            BookView insertedBook = bookMapper.getById(5L);
            BookView expectedBook = new BookView(
                    5L,
                    "Python入門",
                    "パイソン ニュウモン",
                    "佐藤花子",
                    1L,
                    "技術出版社",
                    100L,
                    "テストユーザー",
                    2500,
                    false,
                    currentTime,
                    currentTime
            );
            assertThat(insertedBook).isEqualTo(expectedBook);
        }

        @Test
        void saveShouldInsertBookWithSpecifiedId() {
            // 事前にid=10のデータが存在しないことを検証
            BookView temp = bookMapper.getById(10L);
            assertThat(temp).isEqualTo(null);

            LocalDateTime currentTime = LocalDateTime.of(2025, 5, 4, 13, 20, 10);

            Book book = new Book(
                    10L,
                    "Go入門",
                    "ゴー ニュウモン",
                    "山本一郎",
                    1L,
                    100L,
                    2800,
                    false,
                    currentTime,
                    currentTime
            );
            int affectedRows = bookMapper.save(book);
            // 挿入された行数が1であること
            assertThat(affectedRows).isEqualTo(1);
            // mybatisのuseGeneratedKey機能が正しく挿入されたデータのidを賦与
            assertThat(book.getId()).isEqualTo(10L);

            BookView insertedBook = bookMapper.getById(10L);
            BookView expectedBook = new BookView(
                    10L,
                    "Go入門",
                    "ゴー ニュウモン",
                    "山本一郎",
                    1L,
                    "技術出版社",
                    100L,
                    "テストユーザー",
                    2800,
                    false,
                    currentTime,
                    currentTime
            );
            assertThat(insertedBook).isEqualTo(expectedBook);
        }

        /**
         * idのincrement機能をテストする
         *
         * 1. 最初にID指定なしのINSERTテストし、id = 5のデータをINSERTする
         * 2. 次にID指定でid = 10 のデータをINSERTする
         * 3. 最後にもう一度ID指定なしで登録し、正しくid = 11のプライマリーキーが生成されることを検証
         */
        @Test
        void saveShouldRespectAutoIncrementAfterManualIdInsertion() {
            // ----------------------------------- 1 -----------------------------------------
            //現在の最大idが4で登録されていることを検証
            Long currentMaxId = booksTestMapper.getMaxId();
            assertThat(currentMaxId).isEqualTo(4L);

            LocalDateTime currentTime = LocalDateTime.of(2025, 5, 4, 13, 20, 10);

            Book book = new Book(
                    null,
                    "Java実践ガイド",
                    "ジャバ ジッセン ガイド",
                    "田中太郎",
                    1L,
                    100L,
                    3200,
                    false,
                    currentTime,
                    currentTime
            );
            int affectedRows = bookMapper.save(book);
            // 挿入された行数が1であること
            assertThat(affectedRows).isEqualTo(1);
            // mybatisのuseGeneratedKey機能が正しく挿入されたデータのidを賦与
            assertThat(book.getId()).isEqualTo(5L);

            BookView insertedBook = bookMapper.getById(5L);
            BookView expectedBook = new BookView(
                    5L,
                    "Java実践ガイド",
                    "ジャバ ジッセン ガイド",
                    "田中太郎",
                    1L,
                    "技術出版社",
                    100L,
                    "テストユーザー",
                    3200,
                    false,
                    currentTime,
                    currentTime
            );
            assertThat(insertedBook).isEqualTo(expectedBook);

            // ----------------------------------- 2 -----------------------------------------
            Book book2 = new Book(
                    10L,
                    "Kotlin実践ガイド",
                    "コトリン ジッセン ガイド",
                    "山田花子",
                    1L,
                    100L,
                    3500,
                    false,
                    currentTime,
                    currentTime
            );
            int affectedRows2 = bookMapper.save(book2);
            assertThat(affectedRows2).isEqualTo(1);
            assertThat(book2.getId()).isEqualTo(10L);

            BookView insertedBook2 = bookMapper.getById(10L);
            BookView expectedBook2 = new BookView(
                    10L,
                    "Kotlin実践ガイド",
                    "コトリン ジッセン ガイド",
                    "山田花子",
                    1L,
                    "技術出版社",
                    100L,
                    "テストユーザー",
                    3500,
                    false,
                    currentTime,
                    currentTime
            );
            assertThat(insertedBook2).isEqualTo(expectedBook2);

            // ----------------------------------- 3 -----------------------------------------
            Book book3 = new Book(
                    null,
                    "Spring Bootガイド",
                    "スプリング ブート ガイド",
                    "鈴木一郎",
                    1L,
                    100L,
                    3800,
                    false,
                    currentTime,
                    currentTime
            );
            int affectedRows3 = bookMapper.save(book3);
            assertThat(affectedRows3).isEqualTo(1);
            assertThat(book3.getId()).isEqualTo(11L);

            BookView insertedBook3 = bookMapper.getById(11L);
            BookView expectedBook3 = new BookView(
                    11L,
                    "Spring Bootガイド",
                    "スプリング ブート ガイド",
                    "鈴木一郎",
                    1L,
                    "技術出版社",
                    100L,
                    "テストユーザー",
                    3800,
                    false,
                    currentTime,
                    currentTime
            );
            assertThat(insertedBook3).isEqualTo(expectedBook3);
        }

        /**
         * 主キー重複の場合の動作をテスト
         *
         * テストデータとして、id=1~4のデータが事前に挿入される。
         * このテスト内でid=1で指定してinsertを試すと、主キー重複のエラーになる。
         */
        @Test
        void saveShouldThrowDuplicateKeyExceptionWhenIdIsDuplicated() {
            Book book = new Book(
                    1L,
                    "Python入門",
                    "パイソン ニュウモン",
                    "佐藤花子",
                    1L,
                    100L,
                    2500,
                    false,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            assertThrows(DuplicateKeyException.class, () -> bookMapper.save(book));
        }

        /**
         * 以下の動作を検証する：
         * 指定した外部キーが存在しない場合はDataIntegrityViolationExceptionが投げられ、
         * その中にSQLIntegrityConstraintViolationExceptionとエラーコード1452が含まれる
         */
        @Test
        void saveShouldThrowDataIntegrityViolationExceptionWhenPublisherIdDoesNotExist() {
            Book book = new Book(
                    null,
                    "Python入門",
                    "パイソン ニュウモン",
                    "佐藤花子",
                    999L,
                    100L,
                    2500,
                    false,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class, () -> bookMapper.save(book));
            Throwable rootCause = e.getRootCause();
            assertThat(rootCause instanceof SQLIntegrityConstraintViolationException).isTrue();
            int errorCode = ((SQLIntegrityConstraintViolationException) rootCause).getErrorCode();
            assertThat(errorCode).isEqualTo(1452);
        }

        /**
         * 以下の動作を検証する：
         * 指定した外部キーが存在しない場合はDataIntegrityViolationExceptionが投げられ、
         * その中にSQLIntegrityConstraintViolationExceptionとエラーコード1452が含まれる
         */
        @Test
        void saveShouldThrowDataIntegrityViolationExceptionWhenUserIdDoesNotExist() {
            Book book = new Book(
                    null,
                    "Python入門",
                    "パイソン ニュウモン",
                    "佐藤花子",
                    1L,
                    999L,
                    2500,
                    false,
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );

            DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class, () -> bookMapper.save(book));
            Throwable rootCause = e.getRootCause();
            assertThat(rootCause instanceof SQLIntegrityConstraintViolationException).isTrue();
            int errorCode = ((SQLIntegrityConstraintViolationException) rootCause).getErrorCode();
            assertThat(errorCode).isEqualTo(1452);
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
        @Autowired
        BooksTestMapper booksTestMapper;

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

            Book nothing = booksTestMapper.getByIdIncludingDeleted(999L);
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

            Book deletedBook = booksTestMapper.getByIdIncludingDeleted(2L);
            assertThat(deletedBook.getIsDeleted()).isTrue();

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
