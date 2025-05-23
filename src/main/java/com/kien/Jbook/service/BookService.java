package com.kien.Jbook.service;

import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookCreate;
import com.kien.Jbook.model.dto.book.BookView;
import jakarta.validation.Valid;

public interface BookService {
    BookView getById(Long id);

    BookBasicInfo registerBook(@Valid BookCreate bookCreate);
}
