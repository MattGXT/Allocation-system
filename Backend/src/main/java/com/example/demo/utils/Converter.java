package com.example.demo.utils;

import cn.hutool.core.date.DateUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class Converter {
    public static Integer parseInt(String integer, Integer defaultVal) {
        if(integer == null || integer.equals("")) {
            return defaultVal;
        }
        try {
            if(Integer.parseInt(integer) <= 0) {
                return  defaultVal;
            }
            return Integer.parseInt(integer);
        } catch (Exception e) {
            return defaultVal;
        }
    }

    public static Long parseLong(String longInt) {
        if(longInt == null) {
            return null;
        }
        try {
            return Long.parseLong(longInt);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date parseDate(String date) {
        if(date == null || date.equals("")) {
            return null;
        }
        try {
            return DateUtil.parseDateTime(date);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> List<T> castList(Object obj, Class<T> clazz)
    {
        List<T> result = new ArrayList<T>();
        if(obj instanceof List<?>)
        {
            for (Object o : (List<?>) obj)
            {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    public static String parseFileSize(Long fileSize) {
        if(fileSize.equals(0L)) {
            return "0K";
        }
        try {
            DecimalFormat decimalFormat=new DecimalFormat("0.00");
            Integer fileBinary = 1024;
            Integer fileMaxSize = 30;
//            if (fileSize/fileBinary/fileBinary > fileMaxSize) {
//                return "greaterThan30M";
//            }
            if (fileSize/fileBinary/fileBinary > 0) {
                return String.valueOf(decimalFormat.format((float)fileSize/fileBinary/fileBinary)) + "M";
            }
            if (fileSize/fileBinary > 0) {
                return String.valueOf(fileSize/fileBinary) + "K";
            }
            return String.valueOf(fileSize) + "B";
        } catch (Exception e) {
            return "0K";
        }
    }

}
