package com.example.websocketchat.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import com.example.websocketchat.domain.member.exception.AuthErrorCode;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException {
    AuthErrorCode errorCode;
}

