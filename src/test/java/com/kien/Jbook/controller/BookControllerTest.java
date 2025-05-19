package com.kien.Jbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BookService bookService;

    @Nested
    class GetBookByIdTest {

        @Test
        void return200AndBookWhenExists() throws Exception {
            Long bookId = 1L;
            BookView bookView = new BookView(
                    bookId,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    "技術出版社",
                    100L,
                    "テストユーザー",
                    2500,
                    false,
                    LocalDateTime.of(2025, 4, 28, 10, 0),
                    LocalDateTime.of(2025, 4, 28, 10, 0)
            );
            when(bookService.getById(bookId)).thenReturn(bookView);

            mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookId))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(bookView)));

            verify(bookService, times(1)).getById(bookId);
        }

        @Test
        void return204WhenNotFound() throws Exception {
            Long bookId = 1L;
            when(bookService.getById(bookId)).thenReturn(null);

            mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookId))
                    .andExpect(MockMvcResultMatchers.status().isNoContent());

            verify(bookService, times(1)).getById(bookId);
        }

        @Test
        void return400WhenIdIsNegative() throws Exception {
            Long negativeId = -1L;
            List<Map<String, String>> expectedResponse = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("id", "-1");
            map.put("message", "入力された値が無効です。");
            expectedResponse.add(map);

            ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/books/" + negativeId));
            resultActions.andExpect(MockMvcResultMatchers.status().isBadRequest());
            resultActions.andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }

        @Test
        void return400WhenIdIsZero() throws Exception {
            Long zeroId = 0L;
            List<Map<String, String>> expectedResponse = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            map.put("id", "0");
            map.put("message", "入力された値が無効です。");
            expectedResponse.add(map);

            mockMvc.perform(MockMvcRequestBuilders.get("/books/" + zeroId))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }

        @Test
        void return400WhenIdTypeIsMismatchedFloat() throws Exception {
            double doubleId = 1.5;
            Map<String, String> expectedResponse = new HashMap<>();
            expectedResponse.put("id", "1.5");
            expectedResponse.put("message", "パラメータの型が間違っています");

            mockMvc.perform(MockMvcRequestBuilders.get("/books/" + doubleId))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }

        @Test
        void return400WhenIdTypeIsMismatchedStr() throws Exception {
            String strId = "abc";
            Map<String, String> expectedResponse = new HashMap<>();
            expectedResponse.put("id", "abc");
            expectedResponse.put("message", "パラメータの型が間違っています");

            mockMvc.perform(MockMvcRequestBuilders.get("/books/" + strId))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }

        @Test
        void return400WhenNoParam() throws Exception {
            Map<String, String> expectedResponse = new HashMap<>();
            expectedResponse.put("GET", "/books");
            expectedResponse.put("message", "無効なリクエストです。URLをチェックしてください。");

            mockMvc.perform(MockMvcRequestBuilders.get("/books/"))
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }
    }
}
