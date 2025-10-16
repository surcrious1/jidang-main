package com.jidang.user;

import lombok.Getter;


@Getter
public enum UserRole { //enum은 열거형
    //관리자,사용자를 의미하는 상수 선언
    ADMIN("ROLE_ADMIN"),
    USER("ROLE_USER");

    UserRole(String value) {
        this.value = value;
    }

    private String value;
}
