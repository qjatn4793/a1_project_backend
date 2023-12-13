package com.project.a1.response;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
public class ApiResponseHeader {
    private final Integer resultCode;
    private final String errorCode;
    private final String resultMessage;
    private final Boolean isSuccessful;

    public static ApiResponseHeader success() {
        return ApiResponseHeader.builder()
                .resultCode(0)
                .resultMessage("success")
                .isSuccessful(true)
                .build();
    }

    public static ApiResponseHeader success(@NonNull final String resultMessage) {
        return ApiResponseHeader.builder()
                .resultCode(0)
                .resultMessage(resultMessage)
                .isSuccessful(true)
                .build();
    }

    public static ApiResponseHeader error(@NonNull final Integer resultCode,
                                          @NonNull final String resultMessage) {
        return ApiResponseHeader.builder()
                .resultCode(resultCode)
                .resultMessage(resultMessage)
                .isSuccessful(false)
                .build();
    }

    public static ApiResponseHeader error(@NonNull final Integer resultCode,
                                          @NonNull final String errorCode,
                                          @NonNull final String resultMessage) {
        return ApiResponseHeader.builder()
                .resultCode(resultCode)
                .errorCode(errorCode)
                .resultMessage(resultMessage)
                .isSuccessful(false)
                .build();
    }
}
