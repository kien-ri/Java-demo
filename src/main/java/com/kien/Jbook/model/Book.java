package com.kien.Jbook.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Book {
    private Long id;
    private String title;
    private String titleKana;
    private String author;
    private Long publisherId;
    private Long userId;
    private Integer price;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
