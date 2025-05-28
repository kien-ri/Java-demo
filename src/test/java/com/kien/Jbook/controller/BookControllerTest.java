package com.kien.Jbook.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kien.Jbook.common.CustomException;
import com.kien.Jbook.model.dto.book.BookBasicInfo;
import com.kien.Jbook.model.dto.book.BookUpdate;
import com.kien.Jbook.model.dto.book.BookView;
import com.kien.Jbook.service.BookService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    class UpdateTest{
        @Test
        void return200WhenUpdateSucceeds() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    100L,
                    4200
            );
            BookBasicInfo expectedResult = new BookBasicInfo(1L, "Kotlin応用ガイド");

            when(bookService.update(any())).thenReturn(expectedResult);

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(expectedResult)));

            verify(bookService, times(1)).update(any());
        }

        @Test
        void return400WhenInvalidId() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    -1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    100L,
                    4200
            );
            Map<String, Object> error = Map.of(
                    "id", -1L,
                    "message", "入力された値が無効です。"
            );

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(List.of(error))));

            verify(bookService, never()).update(any());
        }

        @Test
        void return400WhenInvalidPublisherId() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    -1L,
                    100L,
                    4200
            );
            Map<String, Object> error = Map.of(
                    "publisherId", -1L,
                    "message", "入力された値が無効です。"
            );

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(List.of(error))));

            verify(bookService, never()).update(any());
        }

        @Test
        void return400WhenInvalidUserId() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    -1L,
                    4200
            );
            Map<String, Object> error = Map.of(
                    "userId", -1L,
                    "message", "入力された値が無効です。"
            );

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(List.of(error))));

            verify(bookService, never()).update(any());
        }

        @Test
        void return400WhenPriceIsNegative() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    100L,
                    -1
            );
            Map<String, Object> error = Map.of(
                    "price", -1L,
                    "message", "入力された値が無効です。"
            );

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().json(objectMapper.writeValueAsString(List.of(error))));

            verify(bookService, never()).update(any());
        }

        @Test
        void return404WhenPublisherIdNotExist() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    999L,
                    100L,
                    4200
            );

            CustomException ex = new CustomException(
                    "存在しない外部キーです。",
                    HttpStatus.NOT_FOUND,
                    "publisherId",
                    999L
            );
            when(bookService.update(any())).thenThrow(ex);

            Map<String, Object> expected = Map.of(
                    "publisherId", 999L,
                    "message", "存在しない外部キーです。"
            );

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(objectMapper.writeValueAsString(expected)));

            verify(bookService, times(1)).update(any());
        }

        @Test
        void return404WhenUserIdNotExist() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    999L,
                    4200
            );

            CustomException ex = new CustomException(
                    "存在しない外部キーです。",
                    HttpStatus.NOT_FOUND,
                    "userId",
                    999L
            );
            when(bookService.update(any())).thenThrow(ex);

            Map<String, Object> expected = Map.of(
                    "userId", 999L,
                    "message", "存在しない外部キーです。"
            );

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(objectMapper.writeValueAsString(expected)));

            verify(bookService, times(1)).update(any());
        }

        @Test
        void return404WhenBookNotFound() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    999L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    100L,
                    4200
            );

            CustomException ex = new CustomException(
                    "指定IDの書籍情報が存在しません",
                    HttpStatus.NOT_FOUND,
                    "id",
                    999L
            );
            when(bookService.update(any())).thenThrow(ex);

            Map<String, Object> expected = Map.of(
                    "id", 999L,
                    "message", "指定IDの書籍情報が存在しません"
            );

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isNotFound())
                    .andExpect(content().json(objectMapper.writeValueAsString(expected)));

            verify(bookService, times(1)).update(any());
        }

        @Test
        void return500WhenUnexpectedError() throws Exception {
            BookUpdate bookUpdate = new BookUpdate(
                    1L,
                    "Kotlin応用ガイド",
                    "コトリン オウヨウ ガイド",
                    "佐藤次郎",
                    1L,
                    100L,
                    4200
            );

            when(bookService.update(any())).thenThrow(new RuntimeException("予想外のエラー"));

            Map<String, String> expected = Map.of(
                    "error", "予想外のエラーが発生しました。エラー内容：予想外のエラー"
            );

            mockMvc.perform(put("/books")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(bookUpdate)))
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().json(objectMapper.writeValueAsString(expected)));

            verify(bookService, times(1)).update(any());
        }
    }
}
