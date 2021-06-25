package com.example.demo.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Slf4j
@Component
public class ResponseDto<T> {
    private String code;

    private String msg;

    private T data;

    public ResponseDto() {
    }

    public ResponseDto(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = null;
    }
}
