package com.example.demo.utils;

import org.springframework.stereotype.Component;

@Component
public class ResponseUtil {

    public static ResponseDto success(Object object){
        ResponseDto result = new ResponseDto();
        result.setCode("0");
        result.setMsg("successs");
        result.setData(object);
        return result;
    }

    public static ResponseDto error(String errorCode, String errorMsg){
        ResponseDto result = new ResponseDto();
        result.setCode(errorCode);
        result.setMsg(errorMsg);
        result.setData(null);
        return result;
    }

}
