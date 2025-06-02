package com.kien.Jbook.common;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Getter;
import org.springframework.http.HttpStatus;

// 基本はグローバルハンドラで処理されてから必要なプロパティだけを返す形だが、
// 一括登録処理の時など、エラーをthrowではなく、戻り値の一部として設定される場合、必要なプロパティだけ出力されるように、Json属性を指定
@JsonIncludeProperties({"message", "httpStatus", "field", "value"})
@Getter
public class CustomException extends RuntimeException {
    private HttpStatus httpStatus;
    private String field;
    private Object value;

    public CustomException(String message, HttpStatus httpStatus, String field, Object value) {
        super(message);
        this.httpStatus = httpStatus;
        this.field = field;
        this.value = value;
    }
}
