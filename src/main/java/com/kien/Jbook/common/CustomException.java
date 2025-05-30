package com.kien.Jbook.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@EqualsAndHashCode
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
