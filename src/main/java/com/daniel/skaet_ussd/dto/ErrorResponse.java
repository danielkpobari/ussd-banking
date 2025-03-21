package com.daniel.skaet_ussd.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
    private HttpStatus status;
    private LocalDateTime timestamp;

    public ErrorResponse(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
        this.timestamp = LocalDateTime.now();

    }

//    public ErrorResponse(String code, String message, HttpStatus status) {
//        this.code = code;
//        this.message = message;
//        this.status = status;
//        this.timestamp = LocalDateTime.now();
//    }
}
