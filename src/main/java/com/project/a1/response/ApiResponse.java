package com.project.a1.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.a1.response.error.ErrorCode;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@Getter
@Setter
public class ApiResponse<T> {
    @JsonProperty("header")
    private ApiResponseHeader header;
    private T data;

    public static <T> ApiResponse<T> success(@NonNull final T data) {
        return ApiResponse.<T>builder()
                .header(ApiResponseHeader.success())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .header(ApiResponseHeader.success())
                .build();
    }

    public static <T> ApiResponse<T> error(@NonNull final ErrorCode errorCode) {
        return ApiResponse.<T>builder()
            .header(ApiResponseHeader.error(errorCode.getCode(), errorCode.getMessage()))
            .build();
    }

    public static <T> ApiResponse<T> error(@NonNull final ErrorCode errorCode,
        @NonNull final T data) {
        return ApiResponse.<T>builder()
            .header(ApiResponseHeader.error(errorCode.getCode(), errorCode.getMessage()))
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(@NonNull final ErrorCode errorCode,
        @NonNull final String resultMessage) {
        return ApiResponse.<T>builder()
            .header(ApiResponseHeader.error(errorCode.getCode(), resultMessage))
            .build();
    }

    public static <T> ApiResponse<T> error(@NonNull final ErrorCode errorCode,
        @NonNull final String resultMessage,
        @NonNull final T data) {
        return ApiResponse.<T>builder()
            .header(ApiResponseHeader.error(errorCode.getCode(), resultMessage))
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> error(@NonNull final HttpStatus httpStatus) {
        return ApiResponse.<T>builder()
            .header(ApiResponseHeader.error(httpStatus.value(), httpStatus.getReasonPhrase()))
            .build();
    }
}
