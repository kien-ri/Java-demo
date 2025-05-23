package com.kien.Jbook.model.dto.book;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookBasicInfo {
    private Long id;
    private String title;
}
