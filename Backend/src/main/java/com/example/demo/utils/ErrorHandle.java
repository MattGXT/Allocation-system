package com.example.demo.utils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;

@ControllerAdvice
@ResponseBody
@Slf4j
public class ErrorHandle {
    @ExceptionHandler(BisException.class)
    public ResponseDto handleHttpMessageNotReadableException(BisException ex) {
        //返回异常处理
        return ResponseUtil.error(ex.getCode().toString(),ex.getMessage());
    }
}
