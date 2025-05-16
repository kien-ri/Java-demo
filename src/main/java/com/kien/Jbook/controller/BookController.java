package com.kien.Jbook.controller;

import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<BookView> getBookById(@PathVariable @Positive Long id) {
        BookView bookView = bookService.getBookById(id);
        if (bookView == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(bookView);
        }
    }
}
