package com.kien.Jbook.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface BooksTestMapper {
    @Select("SELECT MAX(id) FROM books")
    Long getMaxId();

    @Update("ALTER TABLE books AUTO_INCREMENT = 5")
    void resetAutoIncrement();
}
