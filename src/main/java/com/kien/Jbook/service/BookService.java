package com.kien.Jbook.service;

import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookUpdate;
import com.kien.Jbook.model.dto.book.BookView;
import jakarta.validation.Valid;

public interface BookService {
    BookView getById(Long id);

    BookBasicInfo update(BookUpdate bookUpdate);
}
