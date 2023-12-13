package com.project.a1.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class ErrorDto {
    private int errorCode;
    private String errorMessage;
    private Optional<String> stackTrace;
}