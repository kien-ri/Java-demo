package com.kien.Jbook.service.impl;

import com.kien.Jbook.common.CustomException;
import com.kien.Jbook.utils.DBExceptionUtils;
import com.kien.Jbook.utils.ValidationUtils;
import com.kien.Jbook.mapper.BookMapper;
import com.kien.Jbook.model.Book;
import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookCreate;
import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static com.kien.Jbook.utils.StringUtils.toCamelCase;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookMapper bookMapper;

    @Value("${messages.errors.invalidValue}")
    private String MSG_INVALID_VALUE = "";

    @Value("${messages.errors.insertError}")
    private String MSG_INSERT_ERROR = "";

    @Value("${messages.errors.noIdGenerated}")
    private String MSG_NO_ID_GENERATED = "";

    @Value("${messages.errors.nonExistentFK}")
    private String MSG_NONEXISTENT_FK = "";

    @Value("${messages.errors.duplicateKey}")
    private String MSG_DUPLICATE_KEY = "";

    @Override
    public BookView getById(Long id) {
        if (id == null || id < 1) {
            throw new CustomException(
                    MSG_INVALID_VALUE,
                    HttpStatus.BAD_REQUEST,
                    "id",
                    id
            );
        }
        BookView bookView = bookMapper.getById(id);
        return bookView;
    }

    @Override
    public BookBasicInfo registerBook(BookCreate bookCreate) {
        // 1. DTO to Entity
        LocalDateTime currentTime = LocalDateTime.now();
        Book book = bookCreate.toEntity(currentTime);

        // 2. パラメータのバリデーション
        validateBookParam(book);

        // 3. INSERT実行
        int insertedCount = -1;
        try {
            insertedCount = bookMapper.save(book);
        } catch (DuplicateKeyException e) {
            // 3.1 主キー重複エラー
            throw new CustomException(
                    MSG_DUPLICATE_KEY,
                    HttpStatus.CONFLICT,
                    "id",
                    bookCreate.getId()
            );
        } catch (DataIntegrityViolationException e) {
            // 3.2 外部キー存在しないエラー
            if (DBExceptionUtils.isForeignKeyViolation(e)) {
                String errorMsg = e.getMessage() != null ? e.getMessage() : "";
                String propertyName = toCamelCase(DBExceptionUtils.extractForeignKeyColumn(errorMsg));
                Object propertyValue = null;
                try {
                    Field property = BookCreate.class.getDeclaredField(propertyName);
                    property.setAccessible(true);
                    propertyValue = property.get(bookCreate);
                } catch (NoSuchFieldException | IllegalAccessException ex) {
                    propertyValue = null;
                }
                throw new CustomException(
                        MSG_NONEXISTENT_FK,
                        HttpStatus.NOT_FOUND,
                        propertyName,
                        propertyValue
                );
            }
            throw e;
        }

        // 4. INSERT結果の検証
        // 4.1 挿入件数のチェック
        if (insertedCount <= 0) {
            throw new CustomException(
                    MSG_INSERT_ERROR,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    null,
                    null
            );
        }
        // 4.2 インクリメントのIDが付与されたかをチェック(Mybatis UseGeneratedKeys)
        Long bookId = book.getId();
        if (bookId == null) {
            throw new CustomException(
                    MSG_NO_ID_GENERATED,
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "id",
                    null
            );
        }

        // 5. 戻り値DTO構成
        return new BookBasicInfo(
                bookId,
                book.getTitle()
        );
    }

    private void validateBookParam(Book book) {
        // Validate book ID
        ValidationUtils.validatePositiveId(
                book.getId(),
                "id",
                MSG_INVALID_VALUE
        );
        // Validate publisher ID
        ValidationUtils.validatePositiveId(
                book.getPublisherId(),
                "publisherId",
                MSG_INVALID_VALUE
        );
        // Validate user ID
        ValidationUtils.validatePositiveId(
                book.getUserId(),
                "userId",
                MSG_INVALID_VALUE
        );
        // Validate price
        if (book.getPrice() != null && book.getPrice() < 0) {
            throw new CustomException(
                    MSG_INVALID_VALUE,
                    HttpStatus.BAD_REQUEST,
                    "price",
                    book.getPrice()
            );
        }
    }
}
