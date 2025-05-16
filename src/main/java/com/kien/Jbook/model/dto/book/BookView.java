package com.kien.Jbook.model.dto.book;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookView {
    private Long id;
    private String title;
    private String titleKana;
    private String author;
    private Long publisherId;
    private String publisherName;
    private Long userId;
    private String userName;
    private Integer price;
    private Boolean isDeleted;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
