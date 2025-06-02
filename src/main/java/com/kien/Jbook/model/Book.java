package com.kien.Jbook.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Book {
    // TODO: 整理、修正予定
    public static final String FIELD_ID = "id";
    private Long id;

    private String title;

    private String titleKana;

    private String author;

    public static final String FIELD_PUBLISHER_ID = "publisherId";
    private Long publisherId;

    public static final String FIELD_USER_ID = "userId";
    private Long userId;

    public static final String FIELD_PRICE = "price";
    private Integer price;

    private Boolean isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
