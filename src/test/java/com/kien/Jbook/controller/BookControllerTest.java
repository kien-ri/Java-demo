package com.kien.Jbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookCreate;
import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(bookView)));

            verify(bookService, times(1)).getById(bookId);
        }

        @Test
        void return204WhenNotFound() throws Exception {
            Long bookId = 1L;
            when(bookService.getById(bookId)).thenReturn(null);

            mockMvc.perform(MockMvcRequestBuilders.get("/books/" + bookId))
                    .andExpect(status().isNoContent());

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
            resultActions.andExpect(status().isBadRequest());
            resultActions.andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

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
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }

        @Test
        void return400WhenIdTypeIsMismatchedFloat() throws Exception {
            double doubleId = 1.5;
            Map<String, String> expectedResponse = new HashMap<>();
            expectedResponse.put("id", "1.5");
            expectedResponse.put("message", "パラメータの型が間違っています");

            mockMvc.perform(MockMvcRequestBuilders.get("/books/" + doubleId))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }

        @Test
        void return400WhenIdTypeIsMismatchedStr() throws Exception {
            String strId = "abc";
            Map<String, String> expectedResponse = new HashMap<>();
            expectedResponse.put("id", "abc");
            expectedResponse.put("message", "パラメータの型が間違っています");

            mockMvc.perform(MockMvcRequestBuilders.get("/books/" + strId))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }

        @Test
        void return400WhenNoParam() throws Exception {
            Map<String, String> expectedResponse = new HashMap<>();
            expectedResponse.put("GET", "/books");
            expectedResponse.put("message", "無効なリクエストです。URLをチェックしてください。");

            mockMvc.perform(MockMvcRequestBuilders.get("/books/"))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

            verify(bookService, never()).getById(any());
        }
    }

    @Nested
    class RegisterTest{
        @Test
        public void return200WhenRegisterWithoutId() throws Exception {
            BookCreate bookCreate = new BookCreate(
                    null,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    100L,
                    2500
                    );

            BookBasicInfo expectedResult = new BookBasicInfo(1L, "Kotlin入門");
            when(bookService.register(bookCreate)).thenReturn(expectedResult);

            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookCreate)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

            verify(bookService, times(1)).register(any());
        }

        @Test
        void return200_whenRegisterWithId() throws Exception {
            BookCreate bookCreate = new BookCreate(
                    222L,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    100L,
                    2500
                    );

            BookBasicInfo expectedResult = new BookBasicInfo(222L, "Kotlin入門");

            when(bookService.register(any(BookCreate.class))).thenReturn(expectedResult);

            mockMvc.perform(post("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookCreate)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

            verify(bookService, times(1)).register(any(BookCreate.class));
        }

        @Test
        public void testReturn400WhenIdIsNegative() throws Exception {
            BookCreate bookCreate = new BookCreate(
                    -1L,
                    "Kotlin入門",
                    "コトリン ニュウモン",
                    "山田太郎",
                    1L,
                    100L,
                    2500
            );

            Map<String, String>[] expectedResponseBody = new Map[] {
                    Map.of(
                            "id", -1L,
                            "message", "入力された値が無効です"
                    )
            };
            String expectedJson = objectMapper.writeValueAsString(expectedResponseBody);

            mockMvc.perform(post("/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookCreate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(expectedJson));

            verify(bookService, never()).register(any());
        }
    }
}
