package com.kien.Jbook.service;

import com.kien.Jbook.model.dto.book.BookView;

public interface BookService {
    BookView getById(Long id);
}
