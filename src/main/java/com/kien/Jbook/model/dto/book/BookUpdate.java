package com.kien.Jbook.model.dto.book;

import com.kien.Jbook.model.Book;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookUpdate {
    @Min(1)
    private final Long id;

    private final String title;

    private final String titleKana;

    private final String author;

    @Min(1)
    private final Long publisherId;

    @Min(1)
    private final Long userId;

    @Min(0)
    private final Integer price;

    public Book toEntity(LocalDateTime updatedAt) {
        return new Book(
                id,
                title,
                titleKana,
                author,
                publisherId,
                userId,
                price,
                false,
                null,
                updatedAt
        );
    }
}
