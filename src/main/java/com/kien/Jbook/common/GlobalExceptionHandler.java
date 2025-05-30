package com.kien.Jbook.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @Value("${messages.errors.typeMissmatch}")
    String MSG_TYPE_MISSMATCH = "";

    @Value("${messages.errors.invalidValue}")
    String MSG_INVALID_VALUE = "";

    @Value("${messages.errors.invalidRequest}")
    String MSG_INVALID_REQUEST = "";

    @Value("${messages.errors.unexpectedError}")
    String MSG_UNEXPECTED_ERROR = "";

    String MSG_STR = "message";

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustomException(CustomException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(e.getField(), e.getValue());
        responseBody.put(MSG_STR, e.getMessage());
        return ResponseEntity.status(e.getHttpStatus()).body(responseBody);
    }

    /**
     * request body のプロパティが要件を満たさないエラー
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<Map<String, Object>>> handleValidationExceptions(MethodArgumentNotValidException e) {
        List<Map<String, Object>> errors = e.getBindingResult().getFieldErrors().stream()
                .map(error -> Map.of(
                        error.getField(), error.getRejectedValue(),
                        MSG_STR, MSG_INVALID_VALUE
                )).collect(Collectors.toList());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * パラメータの変数型が違うエラー
     * @param e
     * @return
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(e.getName(), e.getValue());
        responseBody.put(MSG_STR, MSG_TYPE_MISSMATCH);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    /**
     * パラメータの値が要件を満たさないエラー(id　> 0 など)
     * @param e
     * @return
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<List<Map<String, String>>> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
        List<Map<String, String>> errors = new ArrayList<>();
        List<ParameterValidationResult> validationResults = e.getParameterValidationResults();

        for (ParameterValidationResult result : validationResults) {
            if (!result.getResolvableErrors().isEmpty()) {
                String param = result.getMethodParameter().getParameterName();
                if (param == null) {
                    param = "unknown";
                }
                String rejectedValue = result.getArgument() != null ? result.getArgument().toString() : "null";

                Map<String, String> errorMap = new HashMap<>();
                errorMap.put(param, rejectedValue);
                errorMap.put(MSG_STR, MSG_INVALID_VALUE);

                errors.add(errorMap);
            }
        }

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * リクエストURLの間違いで発生するエラー
     * @param e
     * @return
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException e) {
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put(String.valueOf(e.getHttpMethod()), "/" + e.getResourcePath());
        responseBody.put(MSG_STR, MSG_INVALID_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> exceptionHandler(RuntimeException e) {
        Map<String, String> responseBody = new HashMap<>();
        responseBody.put("error", MSG_UNEXPECTED_ERROR + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
    }
}
