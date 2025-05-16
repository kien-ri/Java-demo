package com.kien.Jbook.model;

import java.time.LocalDateTime;

public class Book {
    private Long id;
    private String title;
    private String titleKana;
    private String author;
    private Long publisherId;
    private Long userId;
    private Integer price;
    private boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
