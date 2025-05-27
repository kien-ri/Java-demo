package com.kien.Jbook.mapper;

import com.kien.Jbook.model.Book;
import com.kien.Jbook.model.dto.book.BookView;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BookMapper {
    BookView getById(Long id);

    int update(Book book);
}
