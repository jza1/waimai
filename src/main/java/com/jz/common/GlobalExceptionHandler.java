package com.jz.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLIntegrityConstraintViolationException;

/*
全局异常处理
annotations 注解
@ControllerAdvice 通知
 */
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@Slf4j
@ResponseBody
public class GlobalExceptionHandler {
    /**
     * 异常处理方法
     * @ExceptionHandler
     * @return
     */
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class) //对应需要处理的异常信息
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException ex){
        log.error(ex.getMessage());//ex.getMessage()异常信息

        //contains 包含
        if(ex.getMessage().contains("Duplicate entry")){
            String[] split = ex.getMessage().split(" ");
            String msg = split[2] + "已存在";
            return R.error(msg);
        }

        return R.error("未知错误");
    }

    @ExceptionHandler(CustomException.class) //对应需要处理的异常信息
    public R<String> exceptionHandler(CustomException ex){
        log.error(ex.getMessage());//ex.getMessage()异常信息
        return R.error(ex.getMessage());// ex.getMessage()直接获得CustomException中的异常信息
    }
}
