package com.planb.utils;

import com.planb.vo.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//作为springmvc的异常处理器
//@ControllerAdvice
@RestControllerAdvice
public class ProjectExceptionAdvice {
    //拦截所有的异常信息
    @ExceptionHandler(Exception.class)
    public Result doException(Exception ex) {
        //记录日志
        //通知运维
        //通知开发
        ex.printStackTrace();
        return Result.fail("出错了，请检查后再试！");
    }
}
