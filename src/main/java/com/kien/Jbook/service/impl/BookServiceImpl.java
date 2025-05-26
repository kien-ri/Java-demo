package com.kien.Jbook.service.impl;

import com.kien.Jbook.common.CustomException;
import com.kien.Jbook.mapper.BookMapper;
import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookMapper bookMapper;

    @Value("${messages.errors.invalidValue}")
    private String MSG_INVALID_VALUE = "";

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
}
