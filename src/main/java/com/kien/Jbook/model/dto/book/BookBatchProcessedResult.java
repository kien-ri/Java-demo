package com.kien.Jbook.model.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@AllArgsConstructor
public class BookBatchProcessedResult {
    private HttpStatus httpStatus;
    private List<ProcessedBook> successfulItems;
    private List<ProcessedBook> failedItems;
}

