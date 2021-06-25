package com.example.demo.utils;

import lombok.Data;

@Data
public class BisException extends RuntimeException {

    /**
     * 异常编码
     */
    private String code;
    /**
     * 异常消息处理
     */
    private String message;

    public BisException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

}
