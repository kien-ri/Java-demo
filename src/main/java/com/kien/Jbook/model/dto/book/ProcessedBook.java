package com.kien.Jbook.model.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 一括処理の各アイテムの結果を表すDTO。
 */
@Getter
@AllArgsConstructor
public class ProcessedBook{
    private Long id;
    private String title;
    private Exception error;
}
