package com.ktpm.potatoapi.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final Set<String> IGNORED_ATTRIBUTES = Set.of("message", "groups", "payload");

    // xử lí những lỗi chưa xác định được (chưa catch)
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest httpRequest) {
        log.error("Exception: ", e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCode.UNCATEGORIZED.getCode())
                .message(ErrorCode.UNCATEGORIZED.getMessage())
                .path(httpRequest.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.UNCATEGORIZED.getStatusCode()).body(errorResponse);
    }

    // xử lí những lỗi chung (not found, existed,..)
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ErrorResponse> handleAppException(AppException e, HttpServletRequest httpRequest) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(e.getErrorCode().getCode())
                .message(e.getErrorCode().getMessage())
                .path(httpRequest.getRequestURI())
                .build();

        return ResponseEntity
                .status(e.getErrorCode().getStatusCode())
                .body(errorResponse);
    }

    // xử lí lỗi gửi JSON sai định dạng, k parse vào object được
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleJsonParseError(HttpServletRequest httpRequest) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCode.JSON_INVALID.getCode())
                .message(ErrorCode.JSON_INVALID.getMessage())
                .path(httpRequest.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.JSON_INVALID.getStatusCode()).body(errorResponse);
    }

    // xử lí những lỗi khi vi phạm validation từ @RequestBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.MESSAGE_KEY_INVALID;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            ConstraintViolation<?> constraintViolation = exception.getBindingResult()
                    .getAllErrors()
                    .get(0)
                    .unwrap(ConstraintViolation.class);
            log.info(constraintViolation.toString());

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();
            log.info(attributes.toString());

        } catch (IllegalArgumentException ignored) { }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(errorCode.getCode())
                .message(Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage()
                )
                .build();

        return ResponseEntity.status(errorCode.getStatusCode()).body(errorResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String resolvedMessage = message;
        for (Map.Entry<String, Object> entry : attributes.entrySet()) {
            String key = entry.getKey();

            if (IGNORED_ATTRIBUTES.contains(key))
                continue;

            String value = String.valueOf(entry.getValue());
            resolvedMessage = resolvedMessage.replace("{" + key + "}", value);
        }
        return resolvedMessage;
    }

    // xử lí những lỗi vi phạm validation từ form-data, request param, path variable
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e, HttpServletRequest httpRequest) {
        StringBuilder errorMessage = new StringBuilder();

        if (e.getBindingResult().hasErrors()) {
            var errorList = e.getBindingResult().getAllErrors();
            for (int i = 0; i < errorList.size(); i++) {
                errorMessage.append(errorList.get(i).getDefaultMessage());
                errorMessage.append(i == errorList.size() - 1 ? "." : ", ");
            }
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCode.BIND_INVALID.getCode())
                .message(errorMessage.toString())
                .path(httpRequest.getRequestURI())
                .build();

        return ResponseEntity.status(ErrorCode.BIND_INVALID.getStatusCode()).body(errorResponse);
    }
}
