package com.kien.Jbook.utils;

import com.kien.Jbook.common.CustomException;
import org.springframework.http.HttpStatus;

public class ValidationUtils {
    public static void validatePositiveId(Long id, String fieldName, String errorMsg) {
        if (id != null && id <= 0) {
            throw new CustomException(
                    errorMsg,
                    HttpStatus.BAD_REQUEST,
                    fieldName,
                    id
            );
        }
    }
}
