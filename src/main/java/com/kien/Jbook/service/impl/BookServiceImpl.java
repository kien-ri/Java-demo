package com.kien.Jbook.service.impl;

import com.kien.Jbook.common.CustomException;
import com.kien.Jbook.mapper.BookMapper;
import com.kien.Jbook.model.Book;
import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookUpdate;
import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import com.kien.Jbook.utils.DBExceptionUtils;
import com.kien.Jbook.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Value("${messages.errors.nonExistentBook}")
    private String MSG_NON_EXISTENT_BOOK = "";

    @Value("${messages.errors.nonExistentFK}")
    private String MSG_NONEXISTENT_FK = "";

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
    public BookBasicInfo update(BookUpdate bookUpdate) {
        // 1. DTO to Entity
        LocalDateTime current = LocalDateTime.now();
        Book book = bookUpdate.toEntity(current);

        // 2. パラメータのバリデーション
        validateBookParam(book);

        // 3. UPDATE実行
        int updatedCount = -1;
        try {
            updatedCount = bookMapper.update(book);
        } catch (DataIntegrityViolationException e) {
            // 3.1 外部キー存在しないエラー
            if (DBExceptionUtils.isForeignKeyViolation(e)) {
                String errorMsg = e.getMessage() != null ? e.getMessage() : "";
                String propertyName = toCamelCase(DBExceptionUtils.extractForeignKeyColumn(errorMsg));
                Object propertyValue = null;
                try {
                    Field property = BookUpdate.class.getDeclaredField(propertyName);
                    property.setAccessible(true);
                    propertyValue = property.get(bookUpdate);
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

        // 4. 結果検証
        if (updatedCount <= 0) {
            throw new CustomException(
                    MSG_NON_EXISTENT_BOOK,
                    HttpStatus.NOT_FOUND,
                    "id",
                    book.getId()
            );
        }

        // 5. 戻り値DTO構成
        return new BookBasicInfo(
                book.getId(),
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
