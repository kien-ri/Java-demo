package com.kien.Jbook.service;

import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookBatchProcessedResult;
import com.kien.Jbook.model.dto.book.BookCreate;
import com.kien.Jbook.model.dto.book.BookView;
import jakarta.validation.Valid;

import java.util.List;

public interface BookService {
    BookView getById(Long id);

    BookBasicInfo register(@Valid BookCreate bookCreate);

    BookBatchProcessedResult batchRegister(List<BookCreate> bookCreates);
}
