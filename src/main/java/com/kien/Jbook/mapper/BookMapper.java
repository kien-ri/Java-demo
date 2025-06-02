package com.kien.Jbook.mapper;

import com.kien.Jbook.model.Book;
import com.kien.Jbook.model.dto.book.BookView;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BookMapper {
    BookView getById(Long id);

    int save(Book book);

    int batchSaveWithSpecifiedId(List<Book> withId);

    int batchSaveWithoutId(List<Book> withoutId);
}
