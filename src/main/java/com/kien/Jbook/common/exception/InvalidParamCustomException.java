package com.kien.Jbook.common.exception;

public class InvalidParamCustomException extends CustomException {
    private final String field;
    private final Object value;

    public InvalidParamCustomException(String message, String field, Object value) {
        super(message);
        this.field = field;
        this.value = value;
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }
}
