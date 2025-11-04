package com.example.websocketchat.domain.member.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT-001", "유저를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "ACCOUNT-002", "비밀번호가 일치하지 않습니다."),
    Email_Verification_Failed(HttpStatus.BAD_REQUEST, "ACCOUNT-003", "이메일 인증에 실패하셨습니다."),
    User_Already_Exists(HttpStatus.BAD_REQUEST, "ACCOUNT-004", "이미 가입된 유저입니다.");
    private HttpStatus httpStatus;
    private String code;
    private String message;
}

