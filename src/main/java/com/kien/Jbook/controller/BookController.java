package com.kien.Jbook.controller;

import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookBatchProcessedResult;
import com.kien.Jbook.model.dto.book.BookCreate;
import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<BookView> getById(@PathVariable @Positive Long id) {
        BookView bookView = bookService.getById(id);
        if (bookView == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(bookView);
        }
    }

    @PostMapping
    public ResponseEntity<BookBasicInfo> register(@Valid @RequestBody BookCreate bookCreate) {
        BookBasicInfo createdResponse = bookService.register(bookCreate);
        return ResponseEntity.ok(createdResponse);
    }

    @PostMapping("/batch")
    public ResponseEntity<BookBatchProcessedResult> batchRegister(@RequestBody List<BookCreate> bookCreates) {
        BookBatchProcessedResult result = bookService.batchRegister(bookCreates);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}
