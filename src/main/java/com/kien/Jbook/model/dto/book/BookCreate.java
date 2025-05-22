package com.kien.Jbook.model.dto.book;

import com.kien.Jbook.model.Book;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookCreate {
    @Min(1)
    private Long id;

    private String title;

    private String titleKana;

    private String author;

    @Min(1)
    private Long publisherId;

    @Min(1)
    private Long userId;

    @Min(0)
    private Integer price;

    public Book toEntity(LocalDateTime current) {
        return new Book(
                id,
                title,
                titleKana,
                author,
                publisherId,
                userId,
                price,
                false,
                current,
                current
        );
    }
}
