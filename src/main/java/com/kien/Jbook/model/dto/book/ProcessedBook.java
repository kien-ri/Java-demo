package com.kien.Jbook.model.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProcessedBook{
    private Long id;
    private String title;
    private Exception error;
}
