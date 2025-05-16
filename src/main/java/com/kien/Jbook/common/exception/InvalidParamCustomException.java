package com.kien.Jbook.common.exception;

import lombok.Getter;

@Getter
public class InvalidParamCustomException extends CustomException {
    private final String field;
    private final Object value;

    public InvalidParamCustomException(String message, String field, Object value) {
        super(message);
        this.field = field;
        this.value = value;
    }
}
