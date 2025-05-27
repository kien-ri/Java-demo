package com.kien.Jbook.controller;

import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookUpdate;
import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping
    public ResponseEntity<BookBasicInfo> update(@RequestBody @Valid BookUpdate bookUpdate) {
        BookBasicInfo info = bookService.update(bookUpdate);
        return ResponseEntity.ok(info);
    }
}
