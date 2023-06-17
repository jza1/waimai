package com.jz.common;

/**
 * 自定义业务异常类
 * 提示信息传入
 */
public class CustomException extends RuntimeException {
    public CustomException(String message){
        super(message);
    }
}
