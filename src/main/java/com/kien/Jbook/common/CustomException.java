package com.kien.Jbook.common;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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
